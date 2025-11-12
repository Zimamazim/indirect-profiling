@file:DependsOn("../build/classes/kotlin/main")

import jfr_processor.profilelib.*

walkPath("./").forEach { println(it) }

//println("Hello, world!")