package com.github.bartimaeusnek.modmixins.core

import net.minecraftforge.common.config.Configuration
import java.io.File

object LoadingConfig {

    fun loadConfig(file: File) {
        config = Configuration(file)
        fixBibliocraftNetworkVulnerability = config["fixes", "fixBibliocraftNetworkVulnerability", true, "Fixes Bibliocraft Network Vulnerability"].boolean

        if (config.hasChanged())
            config.save()
    }

    private lateinit var config: Configuration
    var fixBibliocraftNetworkVulnerability : Boolean = true
}