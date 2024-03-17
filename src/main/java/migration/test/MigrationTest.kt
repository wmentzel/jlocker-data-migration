package migration.test

import migration.migrate
import migration.test.NewFormatUtil.decrypt
import javax.crypto.SecretKey
import kotlin.test.*
import com.randomlychosenbytes.jlocker.model.Building as NewBuilding
import com.randomlychosenbytes.jlocker.model.Locker as NewLocker
import com.randomlychosenbytes.jlocker.model.LockerCabinet as NewLockerCabinet
import com.randomlychosenbytes.jlocker.model.Module as ModuleWrapper
import com.randomlychosenbytes.jlocker.model.Room as NewRoom
import com.randomlychosenbytes.jlocker.model.Settings as NewSettings
import com.randomlychosenbytes.jlocker.model.Staircase as NewStaircase
import com.randomlychosenbytes.jlocker.model.Task as NewTask

class DataMigrationTest {

    private lateinit var buildings: List<NewBuilding>
    private lateinit var tasks: List<NewTask>
    private lateinit var settings: NewSettings
    private lateinit var superUserKey: SecretKey

    fun setup() {

        val superUserPassword = "11111111"
        val restrictedUserPassword = "22222222"

        val outputJsonFile = createTempFile("jlocker.json")
        migrate(
            inputDatFile = getResourceAsFile("jlocker.dat"),
            outputJsonFile = outputJsonFile,
            superUserPassword = superUserPassword,
            restrictedUserPassword = restrictedUserPassword
        )

        println(outputJsonFile.readText())

        val newDataLoadedFromFile =
            NewFormatUtil.loadFromCustomFile(outputJsonFile, superUserPassword, restrictedUserPassword)
        superUserKey =
            NewFormatUtil.decryptKeyWithString(newDataLoadedFromFile.superUser.encryptedSuperUMasterKeyBase64, superUserPassword)
        buildings = newDataLoadedFromFile.buildings
        tasks = newDataLoadedFromFile.tasks
        settings = newDataLoadedFromFile.settings
    }

    fun numberOfBuildingsShouldBeCorrect() {
        assertEquals(2, buildings.size)
    }

    fun numberOfFloorsShouldBeCorrect() {
        assertEquals(2, buildings[0].floors.size)
        assertEquals(1, buildings[1].floors.size)
    }

    fun numberOfWalksShouldBeCorrect() {
        assertEquals(2, buildings[0].floors[0].walks.size)
        assertEquals(1, buildings[0].floors[1].walks.size)
        assertEquals(1, buildings[1].floors[0].walks.size)
    }

    fun numberOfManagementUnitsShouldBeCorrect() {
        assertEquals(5, buildings[0].floors[0].walks[0].modules.size)
    }

    fun numberOfLockersShouldBeCorrect() {
        val modules: List<ModuleWrapper> = buildings[0].floors[0].walks[0].modules
        assertEquals(3, (modules[2] as NewLockerCabinet).lockers.size)
        assertEquals(3, (modules[1] as NewLockerCabinet).lockers.size)
        assertEquals(3, (modules[0] as NewLockerCabinet).lockers.size)
    }

    fun buildingNamesShouldMatch() {
        assertEquals("main building", buildings[0].name)
        assertEquals("second building", buildings[1].name)
    }

    fun floorNamesShouldMatch() {
        assertEquals("ground floor", buildings[0].floors[0].name)
        assertEquals("1st floor", buildings[0].floors[1].name)
        assertEquals("ground floor", buildings[1].floors[0].name)
    }

    fun walkNamesShouldMatch() {
        assertEquals("main walk", buildings[0].floors[0].walks[0].name)
        assertEquals("second walk", buildings[0].floors[0].walks[1].name)
        assertEquals("-", buildings[0].floors[1].walks[0].name)
        assertEquals("main walk", buildings[1].floors[0].walks[0].name)
    }

    fun lockerIdsShouldMatch() {
        val cabinet: com.randomlychosenbytes.jlocker.model.LockerCabinet =
            (buildings[0].floors[0].walks[0].modules[0] as com.randomlychosenbytes.jlocker.model.LockerCabinet)
        assertEquals("1", cabinet.lockers[0].id)
        assertEquals("2", cabinet.lockers[1].id)
        assertEquals("3", cabinet.lockers[2].id)
    }

    fun roomDataShouldMatch() {
        val room: NewRoom = buildings[0].floors[0].walks[0].modules[3] as NewRoom
        assertEquals(room.name, "Some Classroom")
        assertEquals(room.schoolClassName, "12")
    }

    fun stairCaseDataShouldMatch() {
        val staircase: NewStaircase = buildings[0].floors[0].walks[0].modules[4] as NewStaircase
        assertEquals("Main Staircase", staircase.name)
    }

    fun lockerTypesShouldMatch() {
        val modules: List<ModuleWrapper> = buildings[0].floors[0].walks[0].modules
        assertEquals(modules[0]::class, NewLockerCabinet::class)
        assertEquals(modules[1]::class, NewLockerCabinet::class)
        assertEquals(modules[2]::class, NewLockerCabinet::class)
        assertEquals(modules[3]::class, NewRoom::class)
        assertEquals(modules[4]::class, NewStaircase::class)
    }

    fun shouldHaveCorrectDataForLocker1() {
        val locker: NewLocker =
            (buildings[0].floors[0].walks[0].modules[0] as NewLockerCabinet).lockers[0]
        assertEquals("Olsen", locker.pupil.lastName)
        assertEquals("Peggy", locker.pupil.firstName)
        assertEquals("11", locker.pupil.schoolClassName)
        assertEquals(200, locker.pupil.heightInCm)
        assertEquals("This is some note!", locker.note)
        assertEquals("01.01.2020", locker.pupil.rentedFromDate)
        assertEquals("01.01.2021", locker.pupil.rentedUntilDate)
        assertEquals("12-34-56", locker.lockCode)
        assertEquals(150, locker.pupil.paidAmount)
        assertEquals(50, locker.pupil.previouslyPaidAmount)
        assertFalse(locker.isOutOfOrder)
        //assertTrue(locker.hasContract) // TODO
        assertEquals(locker.currentCodeIndex, 2)

        val codes = locker.getCodes(superUserKey)

        assertEquals("111111", decrypt(locker.encryptedCodes?.get(0), superUserKey))
        assertEquals("222222", decrypt(locker.encryptedCodes?.get(1), superUserKey))
        assertEquals("333333", decrypt(locker.encryptedCodes?.get(2), superUserKey))
        assertEquals("444444", decrypt(locker.encryptedCodes?.get(3), superUserKey))
        assertEquals("555555", decrypt(locker.encryptedCodes?.get(4), superUserKey))
    }

    fun shouldHaveCorrectDataForLocker2() {
        val locker: NewLocker =
            (buildings[0].floors[0].walks[0].modules[0] as NewLockerCabinet).lockers[1]
        assertNotNull(locker.encryptedCodes)
        assertEquals("Draper", locker.pupil.lastName)
        assertEquals("Don", locker.pupil.firstName)
        assertEquals("01.01.2021", locker.pupil.rentedFromDate)
        assertEquals("31.12.2022", locker.pupil.rentedUntilDate)
        assertEquals("12", locker.pupil.schoolClassName)
        assertTrue(locker.isOutOfOrder)
        assertEquals(locker.currentCodeIndex, 0)
        assertEquals("111111", decrypt(locker.encryptedCodes?.get(0), superUserKey))
        assertEquals("222222", decrypt(locker.encryptedCodes?.get(1), superUserKey))
        assertEquals("987654", decrypt(locker.encryptedCodes?.get(2), superUserKey))
        assertEquals("000000", decrypt(locker.encryptedCodes?.get(3), superUserKey))
        assertEquals("000000", decrypt(locker.encryptedCodes?.get(4), superUserKey))
    }

    fun shouldHaveCorrectDataForLocker3() {
        val locker: NewLocker =
            (buildings[0].floors[0].walks[0].modules[0] as NewLockerCabinet).lockers[2]
        assertTrue(locker.isFree)
        assertFalse(locker.isOutOfOrder)
        //assertFalse(locker.hasContract) // TODO
        assertNull(locker.encryptedCodes)
    }

    fun tasksShouldMatch() {
        assertEquals("This is the 1st task!", tasks[0].description)
        assertTrue(tasks[0].isDone)
        assertEquals("This is the 2nd task!", tasks[1].description)
        assertFalse(tasks[1].isDone)
    }
}