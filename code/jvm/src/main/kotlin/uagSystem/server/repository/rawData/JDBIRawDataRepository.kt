package uagSystem.server.repository.rawData

import org.jdbi.v3.core.Handle

class JDBIRawDataRepository(private val handle: Handle) : RawDataRepository