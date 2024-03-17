package migration

import java.io.File
import java.util.*
import kotlin.test.assertEquals

/**
 * This is the main.main windows of the application. It is displayed right after
 * the login-dialog/create new user dialog.i
 */

fun main(args: Array<String>) {

    var superUserPassword: String = ""
    var restrictedUserPassword: String = ""

    assertEquals(1, 2)

    when (args.first()) {
        "INTERACTIVE" -> {
            val scanner = Scanner(System.`in`)
            println("Super nonabstractreps.User password: ")
            superUserPassword = scanner.nextLine()
            println("Restricted nonabstractreps.User password: ")
            restrictedUserPassword = scanner.nextLine()
        }

        "RUN" -> {
            superUserPassword = args[1]
            restrictedUserPassword = args[2]
        }
    }

    val appDir = Util.getAppDir(true)
    println("""* Program directory is: "${appDir.absolutePath}"""")
    val oldJLockerDatFile = File(appDir, "jlocker.dat")
    val newJLockerDatFile = File(appDir, "jlocker.json")
    migrate(oldJLockerDatFile, newJLockerDatFile, superUserPassword, restrictedUserPassword)
    println("""* File conversion successfully completed. The new file can be found here: "${newJLockerDatFile.absolutePath}""")
}

fun migrate(inputDatFile: File, outputJsonFile: File, superUserPassword: String, restrictedUserPassword: String) {
    val oldData =
        OldFormatUtil.loadData(inputDatFile, superUserPassword, restrictedUserPassword)
    val newData = NewFormatUtil.convert(
        oldData,
        superUserPassword,
        restrictedUserPassword,
        NewFormatUtil.getSecretKeys(superUserPassword, oldData.users[0]).first
    )
    NewFormatUtil.saveData(
        outputJsonFile, superUserPassword, restrictedUserPassword,
        newData.buildings,
        newData.settings,
        newData.tasks,
        newData.superUser,
        newData.restrictedUser
    )
}
