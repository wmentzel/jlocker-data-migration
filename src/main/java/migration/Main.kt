package migration

import java.io.File
import java.util.*

/**
 * This is the main.main windows of the application. It is displayed right after
 * the login-dialog/create new user dialog.i
 */
fun main(args: Array<String>) {

    var superUserPassword: String = ""
    var restrictedUserPassword: String = ""

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
    val oldData =
        OldFormatUtil.loadData(oldJLockerDatFile, superUserPassword, restrictedUserPassword)
    val newJLockerDatFile = File(appDir, "jlocker.json")
    val newData = NewFormatUtil.convert(
        oldData,
        superUserPassword,
        restrictedUserPassword,
        NewFormatUtil.getSecretKeys(superUserPassword, oldData.users[0]).first
    )
    NewFormatUtil.saveData(
        newJLockerDatFile, superUserPassword, restrictedUserPassword,
        newData.buildings,
        newData.settings,
        newData.tasks,
        newData.superUser,
        newData.restrictedUser
    )
    println("""* File conversion successfully completed. The new file can be found here: "${newJLockerDatFile.absolutePath}""")
}
