package com.randomlychosenbytes.jlocker.algorithms;

import com.randomlychosenbytes.jlocker.abstractreps.EntityCoordinates;
import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.manager.DataManager;
import com.randomlychosenbytes.jlocker.nonabstractreps.*;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ShortenClassRoomDistances {
    /* *************************************************************************
        Status codes
    **************************************************************************/
    public static final int CLASS_HAS_NO_ROOM = 0;
    public static final int NO_EMPTY_LOCKERS_AVAILABLE = 1;
    public static final int CLASS_HAS_NO_PUPILS = 2;
    public static final int NON_REACHABLE_LOCKERS_EXIST = 3;
    public static final int NO_MINIMUM_SIZE_DEFINED_FOR_ROW = 4;
    public static final int SUCCESS = 5;

    /* *************************************************************************
        Calibration for the algorithm: edge weights
     **************************************************************************/
    private final float buildingToBuildingEdgeWeight = 100.0f;
    private final float walkToWalkEdgeWeight = 5.0f;
    private final float floorToFloorEdgeWeight = 20.0f;
    private final float managementUnitToMUEdgeWeight = 2.0f;
    private final float managementUnitToLockerEdgeWeight = 1.0f;

    private final List<EntityCoordinates<Locker>> allLockersEntityCoordinatesList;
    private final SimpleWeightedGraph<String, DefaultWeightedEdge> managementUnitGraph;

    private final List<EntityCoordinates<Locker>> freeLockersEntityCoordinatesList;
    private final List<EntityCoordinates<Locker>> classLockersEntityCoordinatesList;

    private final String classRoomNodeId;
    private final List<Pair> classLockerToDistancePairList;
    private final List<Pair> freeLockerToDistancePairList;
    private List<String> unreachableLockers;

    private final DataManager dataManager;
    private final String className;
    private final int status;

    public ShortenClassRoomDistances(DataManager dataManager, String classRoomNodeId, String className) {
        this.dataManager = dataManager;
        this.className = className;
        this.classRoomNodeId = classRoomNodeId;

        this.managementUnitGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        this.allLockersEntityCoordinatesList = new LinkedList<>();

        this.freeLockersEntityCoordinatesList = new LinkedList<>();
        this.classLockersEntityCoordinatesList = new LinkedList<>();

        this.classLockerToDistancePairList = new LinkedList<>();
        this.freeLockerToDistancePairList = new LinkedList<>();

        // creates a weighted graph
        connectManagementUnitsAndLockers();
        connectWalksOnFloor();
        connectFloorsByStaircases();
        connectBuildinsByStaircases();

        status = check();
    }

    private int check() {
        // create a list of all free lockers
        // and all the lockers that belong to people of that class
        for (EntityCoordinates<Locker> lockerEntityCoordinates : allLockersEntityCoordinatesList) {
            if (lockerEntityCoordinates.getEntity().isFree()) {
                freeLockersEntityCoordinatesList.add(lockerEntityCoordinates);
            }
            // class locker
            else if (lockerEntityCoordinates.getEntity().getOwnerClass().equals(className)) {
                classLockersEntityCoordinatesList.add(lockerEntityCoordinates);
            }
        }

        if (freeLockersEntityCoordinatesList.isEmpty()) {
            return NO_EMPTY_LOCKERS_AVAILABLE;
        }

        if (classLockersEntityCoordinatesList.isEmpty()) {
            return CLASS_HAS_NO_PUPILS;
        }

        //
        // Sort free lockers, beginning with the one that is the closest one
        //
        unreachableLockers = new LinkedList<>();

        for (EntityCoordinates<Locker> freeLockerECoord : freeLockersEntityCoordinatesList) {
            int dist = getDistance(freeLockerECoord, classRoomNodeId);

            if (dist != -1) {
                freeLockerToDistancePairList.add(new Pair(freeLockerECoord, dist));
            } else {
                // Create list that contains the ids of all lockers that cant 
                // be reached from the classroom
                unreachableLockers.add(freeLockerECoord.getEntity().getId());
            }
        }
        Collections.sort(freeLockerToDistancePairList, new EntityDistanceComparator());

        //
        // Sort class lockers, beginning with the one that is the farthest 
        // distance to the class room
        //
        for (EntityCoordinates<Locker> classLockerECoord : classLockersEntityCoordinatesList) {
            int distance = getDistance(classLockerECoord, classRoomNodeId);

            if (distance != -1) {
                classLockerToDistancePairList.add(new Pair(classLockerECoord, distance));
            } else {
                // Create a list that contains the ids of all lockers that can't
                // be reached from the classroom
                unreachableLockers.add(classLockerECoord.getEntity().getId());
            }
        }
        Collections.sort(classLockerToDistancePairList, new EntityDistanceComparator());
        Collections.reverse(classLockerToDistancePairList);

        // Output how many lockers cant be reached
        if (!unreachableLockers.isEmpty()) {
            return NON_REACHABLE_LOCKERS_EXIST;
        }

        return SUCCESS;
    }

    private void addStatusText() {

    }

    /**
     * Does the actual moving based on the data gathered before.
     * and returns a list of the moving operations.
     */
    public final String execute() {
        String statusMessage = "";
        List<Integer> minSizes = (List<Integer>) dataManager.getSettings().get("LockerMinSizes");

        statusMessage += "Es gibt " + classLockerToDistancePairList.size() + " Schließfächer der Klasse " + className + "\n";
        statusMessage += "Es wurden " + freeLockerToDistancePairList.size() + " freie Schließfächer gefunden!\n\n";
        statusMessage += "Es wurden " + minSizes.size() + " Minmalgrößen (cm) angelegt!\n";

        for (int size : minSizes) {
            statusMessage += size + " ";
        }

        statusMessage += "\n\n";

        for (Pair<EntityCoordinates<Locker>, Integer> classLockerToDistancePair : classLockerToDistancePairList) {
            // search until you find a free locker that is nearer and suits the pupils size
            for (int freeLockerIndex = 0; freeLockerIndex < freeLockerToDistancePairList.size(); freeLockerIndex++) {
                Pair<EntityCoordinates<Locker>, Integer> freeLockerToDistancePair = freeLockerToDistancePairList.get(freeLockerIndex);

                // Is distance of the new locker shorter to the classroom?
                if (classLockerToDistancePair.getY() > freeLockerToDistancePair.getY()) {
                    Locker srcLocker = classLockerToDistancePair.getX().getEntity();
                    Locker destLocker = freeLockerToDistancePair.getX().getEntity();

                    // determine minimum size for this locker
                    int index = freeLockerToDistancePair.getX().getLValue();

                    // if no minimum size exists for this locker row, don't move
                    if (index < minSizes.size()) {
                        // TODO lowest locker has coordinate 4, highest coordinate 0...
                        // this doesn't make sense
                        int lockerMinSize = minSizes.get(Math.abs(index - (minSizes.size() - 1)));

                        if (srcLocker.getOwnerSize() >= lockerMinSize) {
                            statusMessage += srcLocker.getId() + " -> " + destLocker.getId() + "\n";
                            statusMessage += "Besitzergröße: " + classLockerToDistancePair.getX().getEntity().getOwnerSize() + " cm\n";
                            statusMessage += "Minimalgröße: " + lockerMinSize + "\n";

                            float distanceReduction = (1.0f - freeLockerToDistancePair.getY() / ((float) classLockerToDistancePair.getY())) * 100;
                            DecimalFormat df = new DecimalFormat("##.#");
                            statusMessage += "Entfernung verkürzt um: " + df.format(distanceReduction) + "%\n\n";

                            try {
                                dataManager.moveLockers(srcLocker, destLocker, false);
                            } catch (CloneNotSupportedException ex) {
                                JOptionPane.showMessageDialog(null, "Ein schwerwiegender Fehler ist aufgetreten, die Optimierung konnte nicht ausgeführt werden.", "Fehler", JOptionPane.OK_OPTION);
                            }

                            freeLockerToDistancePairList.remove(freeLockerIndex); // this one is now occupied, so remove it

                            String taskText = "Klassenumzug" + " ("
                                    + destLocker.getOwnerClass() + "): "
                                    + srcLocker.getId() + " -> "
                                    + destLocker.getId() +
                                    " Inhaber(in) "
                                    + destLocker.getOwnerName()
                                    + " " + destLocker.getSurname();

                            dataManager.getTasks().add(new Task(taskText));
                            break;
                        }
                    }
                }
            }
        }

        return statusMessage;
    }

    public SimpleWeightedGraph<String, DefaultWeightedEdge> getWeightedGraph() {
        return managementUnitGraph;
    }

    public int getStatus() {
        return status;
    }

    public List<EntityCoordinates<Locker>> getEntityCoordinatesOfFreeLockers() {
        return freeLockersEntityCoordinatesList;
    }

    public String getIdsOfUnreachableLockers() {
        String s = "";

        for (int i = 0; i < unreachableLockers.size(); i++) {
            if (i != 0) {
                s += ", ";
            }

            if (i % 15 == 0) {
                s += "\n";
            }

            s += unreachableLockers.get(i);
        }

        return s;
    }

    /**
     * Connects MangementUnits on a floor with each other and each ManamentUnit
     * with its lockers.
     */
    private void connectManagementUnitsAndLockers() {
        List<Building> buildings = dataManager.getBuildingList();

        for (int b = 0; b < buildings.size(); b++) {
            List<Floor> floors = buildings.get(b).getFloorList();

            for (int f = 0; f < floors.size(); f++) {
                List<Walk> walks = floors.get(f).getWalkList();

                for (int w = 0; w < walks.size(); w++) {
                    List<ManagementUnit> managementUnits = walks.get(w).getManagementUnitList();

                    String prevMUnitID = null;

                    // Connect ManagementUnits with each other
                    for (int m = 0; m < managementUnits.size(); m++) {
                        ManagementUnit munit = managementUnits.get(m);
                        String currentMUnitID = createNodeId(b, f, w, m);

                        managementUnitGraph.addVertex(currentMUnitID);

                        // connect with previous munit
                        if (m > 0) {
                            DefaultWeightedEdge edge = managementUnitGraph.addEdge(prevMUnitID, currentMUnitID);
                            managementUnitGraph.setEdgeWeight(edge, managementUnitToMUEdgeWeight);
                        }

                        // vertices have been connected, set prevMUnitID for next run
                        prevMUnitID = currentMUnitID;

                        if (munit.getType() == ManagementUnit.LOCKERCOLUMN) {
                            List<Locker> lockers = managementUnits.get(m).getLockerList();

                            // connect each locker with its ManagementUnit
                            for (int l = 0; l < lockers.size(); l++) {
                                Locker locker = lockers.get(l);

                                managementUnitGraph.addVertex(locker.getId());

                                // Connect lockers with their ManagmentUnit
                                DefaultWeightedEdge edge = managementUnitGraph.addEdge(currentMUnitID, locker.getId());
                                managementUnitGraph.setEdgeWeight(edge, managementUnitToLockerEdgeWeight);

                                // fill locker in the list containing all
                                // locker coordinates
                                allLockersEntityCoordinatesList.add(new EntityCoordinates(locker, b, f, w, m, l));
                            }
                        }
                    } // end of for(int c = 0; c < managementUnits.size(); c++)
                }  // for (int w = 0; w < walks.size(); w++)
            } // end of for(int f = 0; f < floors.size(); f++)
        } // end of for(int b = 0; b < buildings.size(); b++)
    }

    /**
     * Connects the walks with each other on every floor
     */
    private void connectWalksOnFloor() {
        List<Building> buildings = dataManager.getBuildingList();

        for (int b = 0; b < buildings.size(); b++) {
            List<Floor> floors = buildings.get(b).getFloorList();

            for (int f = 0; f < floors.size(); f++) {
                List<Walk> walks = floors.get(f).getWalkList();

                // we start with w = 1 because we connect every walk with the 
                // walks before
                for (int w = 1; w < walks.size(); w++) {
                    int lastMUnitIndex = walks.get(w - 1).getMus().size() - 1;

                    DefaultWeightedEdge edge = managementUnitGraph.addEdge(createNodeId(b, f, w - 1, lastMUnitIndex), createNodeId(b, f, w, 0));
                    managementUnitGraph.setEdgeWeight(edge, walkToWalkEdgeWeight);
                }
            }
        }
    }

    /**
     * Connects the floors of a building with each other
     */
    private void connectFloorsByStaircases() {
        List<Building> buildings = dataManager.getBuildingList();

        for (int b = 0; b < buildings.size(); b++) {
            List<Floor> floors = buildings.get(b).getFloorList();

            for (int f = 0; f < floors.size(); f++) {
                List<Walk> walks = floors.get(f).getWalkList();

                for (int w = 0; w < walks.size(); w++) {
                    List<ManagementUnit> managementUnits = walks.get(w).getManagementUnitList();

                    for (int m = 0; m < managementUnits.size(); m++) {
                        ManagementUnit munit = managementUnits.get(m);

                        // connect every managementUnit with the munits above that have the same name
                        if (munit.getType() == ManagementUnit.STAIRCASE) {
                            String currentMUnitID = createNodeId(b, f, w, m);

                            List<String> ids = findStaircasesOnFloor(b, f + 1, munit.getStaircase().getSName());

                            for (String id : ids) {
                                DefaultWeightedEdge edge = managementUnitGraph.addEdge(currentMUnitID, id);
                                managementUnitGraph.setEdgeWeight(edge, floorToFloorEdgeWeight);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the IDs of all ManagementUnits on the given floor with the given name
     */
    private List<String> findStaircasesOnFloor(int b, int f, String name) {
        List<Building> buildings = dataManager.getBuildingList();
        List<Floor> floors = buildings.get(b).getFloorList();
        List<String> entityIds = new LinkedList<>();

        // does the floor exist?
        if (floors.size() > f) {
            List<Walk> walks = floors.get(f).getWalkList();

            for (int w = 0; w < walks.size(); w++) {
                List<ManagementUnit> managementUnits = walks.get(w).getManagementUnitList();

                for (int m = 0; m < managementUnits.size(); m++) {
                    ManagementUnit managementUnit = managementUnits.get(m);

                    if (managementUnit.getType() == ManagementUnit.STAIRCASE) {
                        if (managementUnit.getStaircase().getSName().equals(name)) {
                            entityIds.add(createNodeId(b, f, w, m));
                        }
                    }
                }
            }
        }

        return entityIds;
    }

    /**
     * Connects buildings by staircases
     */
    private void connectBuildinsByStaircases() {
        List<Building> buildings = dataManager.getBuildingList();

        // start with b = 1 so we connect with previous buildings
        for (int b = 1; b < buildings.size(); b++) {
            List<Floor> floors = buildings.get(b).getFloorList();

            for (int f = 0; f < floors.size(); f++) {
                List<Walk> walks = floors.get(f).getWalkList();

                for (int w = 0; w < walks.size(); w++) {
                    List<ManagementUnit> managementUnits = walks.get(w).getManagementUnitList();

                    for (int m = 0; m < managementUnits.size(); m++) {
                        ManagementUnit munit = managementUnits.get(m);

                        // connect every managementUnit with the munits above that have the same name
                        if (munit.getType() == ManagementUnit.STAIRCASE) {
                            String staircaseName = munit.getStaircase().getSName();

                            List<String> staircaseIds = findStaircasesForBuilding(b - 1, staircaseName);

                            String currentStaircaseId = createNodeId(b, f, w, m);

                            for (String staircaseId : staircaseIds) {
                                DefaultWeightedEdge edge = managementUnitGraph.addEdge(currentStaircaseId, staircaseId);
                                managementUnitGraph.setEdgeWeight(edge, buildingToBuildingEdgeWeight);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the IDs of all Staircases for a given building
     */
    private List<String> findStaircasesForBuilding(int b, String name) {
        Building building = dataManager.getBuildingList().get(b);
        List<String> entityIds = new LinkedList<>();

        List<Floor> floors = building.getFloorList();

        for (int f = 0; f < floors.size(); f++) {
            List<Walk> walks = floors.get(f).getWalkList();

            for (int w = 0; w < walks.size(); w++) {
                List<ManagementUnit> managementUnits = walks.get(w).getManagementUnitList();

                for (int m = 0; m < managementUnits.size(); m++) {
                    ManagementUnit managementUnit = managementUnits.get(m);

                    if (managementUnit.getType() == ManagementUnit.STAIRCASE) {
                        if (managementUnit.getStaircase().getSName().equals(name)) {
                            entityIds.add(createNodeId(b, f, w, m));
                        }
                    }
                }
            }
        }

        return entityIds;
    }

    /**
     * Returns a value of distance between a locker and a class room.
     */
    private int getDistance(EntityCoordinates<Locker> locker, String classRoomNodeId) {
        String lockerID = locker.getEntity().getId();

        DijkstraShortestPath<String, DefaultWeightedEdge> shortest = new DijkstraShortestPath<>(managementUnitGraph, lockerID, classRoomNodeId);

        GraphPath<String, DefaultWeightedEdge> path = shortest.getPath();

        if (path != null) {
            return (int) path.getWeight();
        }

        return -1; // not reachable 
    }

    private String createNodeId(int b, int f, int w, int m) {
        return b + "-" + f + "-" + w + "-" + m;
    }

    /**
     * Compares the Y value of a pair containing an EntityCoordintes object
     * and distance as integer
     */
    private class EntityDistanceComparator implements Comparator<Pair> {
        @Override
        public int compare(Pair p1, Pair p2) {
            int dist1 = ((Integer) p1.getY());
            int dist2 = ((Integer) p2.getY());

            if (dist1 == dist2) {
                return 0;
            }

            return dist1 > dist2 ? +1 : -1;
        }
    }
}
