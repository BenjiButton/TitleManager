package io.puharesource.mc.titlemanager.internal.reflections

import org.bukkit.Bukkit
import java.util.TreeMap
import java.util.regex.Pattern

object NMSManager {
    private val VERSION_PATTERN: Pattern = Pattern.compile("(v|)[0-9][_.][0-9]+[_.][R0-9]*")
    private val BUKKIT_VERSION_PATTERN: Pattern = Pattern.compile("^(?:v|)([0-9][_.][0-9]+)[_.][0-9]+-R[0-9.]+(?:-SNAPSHOT)?$")
    private val supportedVersions: MutableMap<String, Int> = TreeMap(String.CASE_INSENSITIVE_ORDER)

    val isPackageVersion: Boolean
    val serverVersion: String
    val versionIndex: Int

    init {
        supportedVersions["v1_7_R4"] = 0
        supportedVersions["v1_8_R1"] = 1
        supportedVersions["v1_8_R2"] = 2
        supportedVersions["v1_8_R3"] = 2
        supportedVersions["v1_9_R1"] = 3
        supportedVersions["v1_9_R2"] = 3
        supportedVersions["v1_10_R1"] = 4
        supportedVersions["v1_11_R1"] = 5
        supportedVersions["v1_12_R1"] = 6
        supportedVersions["v1_13_R1"] = 7
        supportedVersions["v1_13_R2"] = 8
        supportedVersions["v1_14_R1"] = 9
        supportedVersions["v1_15_R1"] = 9
        supportedVersions["v1_16_R1"] = 10
        supportedVersions["v1_16_R2"] = 10
        supportedVersions["v1_16_R3"] = 10
        supportedVersions["v1_17_R1"] = 11
        supportedVersions["v1_18_R1"] = 12
        supportedVersions["v1_18_R2"] = 12
        supportedVersions["v1_19_R1"] = 13
        supportedVersions["v1_19_R2"] = 13
        supportedVersions["v1_19_R3"] = 13
        supportedVersions["v1_20_R1"] = 14
        supportedVersions["v1_20_R2"] = 14
        supportedVersions["v1_20_R3"] = 14
        supportedVersions["v1_20_R4"] = 14
        supportedVersions["v1_21_R1"] = 15

        val pkg: String = Bukkit.getServer().javaClass.`package`.name
        var version = pkg.substring(pkg.lastIndexOf(".") + 1)

        isPackageVersion = VERSION_PATTERN.matcher(version).matches()
        if (!isPackageVersion) {
            version = ""
            var versionMatcher = BUKKIT_VERSION_PATTERN.matcher(Bukkit.getServer().bukkitVersion)
            if (versionMatcher.find()) {
                var bukkitVersion = versionMatcher.group(1).replace(".", "_")
                var versionBase = "v${bukkitVersion}_R"
                version = supportedVersions.keys.filter { it.startsWith(versionBase) }.maxOrNull() ?: "${versionBase}1"
            }
        }

        serverVersion = version
        versionIndex = getVersionIndex(version)
    }

    private fun getVersionIndex(version: String) = supportedVersions.getOrElse(version) {
        supportedVersions.filter { it.key.startsWith(version.dropLast(1)) }.values.maxOrNull()
            ?: supportedVersions.values.maxOrNull() ?: -1
    }

    fun getClassProvider(): NMSClassProvider {
        return when (versionIndex) {
            0 -> ProviderProtocolHack
            1 -> Provider18
            2 -> Provider183
            3 -> Provider183
            4 -> Provider110
            5 -> Provider110
            6 -> Provider112
            7 -> Provider113
            8 -> Provider113
            9 -> Provider113
            10 -> Provider116
            11 -> Provider117
            12 -> Provider117
            13 -> Provider119
            14 -> Provider120
            15 -> Provider120
            else -> Provider120
        }
    }
}
