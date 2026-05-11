package app.morphe.patches.youtube.misc.fix.verticalscroll

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.youtube.misc.playservice.is_21_19_or_greater
import app.morphe.patches.youtube.misc.playservice.versionCheckPatch
import app.morphe.util.returnLate
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

val fixVerticalScrollPatch = bytecodePatch(
    description = "Fixes issues with refreshing the feed when the first component is of type EmptyComponent.",
) {

    dependsOn(versionCheckPatch)

    execute {
        // Flag 45782902 causes home/feed scroll stuttering on 21.19+.
        if (is_21_19_or_greater) {
            FeedScrollStutterFeatureFlagFingerprint.method.returnLate(false)
        }

        CanScrollVerticallyFingerprint.let {
            it.method.apply {
                val moveResultIndex = it.instructionMatches.last().index
                val moveResultRegister = getInstruction<OneRegisterInstruction>(moveResultIndex).registerA

                val insertIndex = moveResultIndex + 1
                addInstruction(
                    insertIndex,
                    "const/4 v$moveResultRegister, 0x0",
                )
            }
        }
    }
}
