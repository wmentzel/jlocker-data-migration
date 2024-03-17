package migration.test


/**
 * Since this needs to run with a specifc Java version (7), I decided to use a main function
 * to trigger the tests instead of a test runner like JUnit.
 */
fun main() {
    val dataMigrationTest = DataMigrationTest()
    dataMigrationTest.setup()
    dataMigrationTest.numberOfBuildingsShouldBeCorrect()
    dataMigrationTest.numberOfFloorsShouldBeCorrect()
    dataMigrationTest.numberOfWalksShouldBeCorrect()
    dataMigrationTest.numberOfManagementUnitsShouldBeCorrect()
    dataMigrationTest.numberOfLockersShouldBeCorrect()
    dataMigrationTest.buildingNamesShouldMatch()

    dataMigrationTest.floorNamesShouldMatch()
    dataMigrationTest.walkNamesShouldMatch()
    dataMigrationTest.lockerIdsShouldMatch()

    dataMigrationTest.roomDataShouldMatch()
    dataMigrationTest.stairCaseDataShouldMatch()
    dataMigrationTest.lockerTypesShouldMatch()

    dataMigrationTest.shouldHaveCorrectDataForLocker1()
    dataMigrationTest.shouldHaveCorrectDataForLocker2()
    dataMigrationTest.shouldHaveCorrectDataForLocker3()
}