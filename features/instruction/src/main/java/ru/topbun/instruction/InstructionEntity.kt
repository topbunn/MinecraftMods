package ru.topbun.instruction

import kotlinx.serialization.Serializable
import ru.topbun.ui.R

@Serializable
data class InstructionEntity(
    val titleRes: Int,
    val imageRes: Int,
){

    companion object{

        fun getAddonInstruction() = listOf(
            InstructionEntity(R.string.instr_addon_1, R.drawable.instr_addon_1),
            InstructionEntity(R.string.instr_addon_2, R.drawable.instr_addon_2),
            InstructionEntity(R.string.instr_addon_3, R.drawable.instr_addon_3),
            InstructionEntity(R.string.instr_addon_4, R.drawable.instr_addon_4),
        )

        fun getWorldInstruction() = listOf(
            InstructionEntity(R.string.instr_world_1, R.drawable.instr_world_1),
            InstructionEntity(R.string.instr_world_2, R.drawable.instr_world_2),
            InstructionEntity(R.string.instr_world_3, R.drawable.instr_world_3),
            InstructionEntity(R.string.instr_addon_4, R.drawable.instr_addon_4),
        )


    }

}