package org.example.services

import org.example.models.Mineral
import org.example.utils.MineralLoader

class MineralService {
    fun getMineralList(): List<Mineral> {
        return MineralLoader.loadFromFile()
    }
}