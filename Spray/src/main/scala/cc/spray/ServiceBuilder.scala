package cc.spray

import builders._

trait ServiceBuilder
        extends BasicBuilders
        with PathBuilders
        with DeSerializingBuilders
        with FileResourceDirectoryBuilders