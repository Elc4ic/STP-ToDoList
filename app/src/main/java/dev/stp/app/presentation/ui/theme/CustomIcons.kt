package dev.stp.app.presentation.ui.theme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
object CustomIcons {

    val UnPinned: ImageVector
        get() {
            if (_TablerPinnedOff != null) return _TablerPinnedOff!!

            _TablerPinnedOff = ImageVector.Builder(
                name = "pinned-off",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(3f, 3f)
                    lineToRelative(18f, 18f)
                }
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(15f, 4.5f)
                    lineToRelative(-3.249f, 3.249f)
                    moveToRelative(-2.57f, 1.433f)
                    lineToRelative(-2.181f, 0.818f)
                    lineToRelative(-1.5f, 1.5f)
                    lineToRelative(7f, 7f)
                    lineToRelative(1.5f, -1.5f)
                    lineToRelative(0.82f, -2.186f)
                    moveToRelative(1.43f, -2.563f)
                    lineToRelative(3.25f, -3.251f)
                }
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(9f, 15f)
                    lineToRelative(-4.5f, 4.5f)
                }
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(14.5f, 4f)
                    lineToRelative(5.5f, 5.5f)
                }
            }.build()

            return _TablerPinnedOff!!
        }

    private var _TablerPinnedOff: ImageVector? = null


    val Pinned: ImageVector
        get() {
            if (_TablerPinned != null) return _TablerPinned!!

            _TablerPinned = ImageVector.Builder(
                name = "pinned",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(
                    fill = SolidColor(Color.Black)
                ) {
                    moveTo(16f, 3f)
                    arcToRelative(1f, 1f, 0f, false, true, 0.117f, 1.993f)
                    lineToRelative(-0.117f, 0.007f)
                    verticalLineToRelative(4.764f)
                    lineToRelative(1.894f, 3.789f)
                    arcToRelative(1f, 1f, 0f, false, true, 0.1f, 0.331f)
                    lineToRelative(0.006f, 0.116f)
                    verticalLineToRelative(2f)
                    arcToRelative(1f, 1f, 0f, false, true, -0.883f, 0.993f)
                    lineToRelative(-0.117f, 0.007f)
                    horizontalLineToRelative(-4f)
                    verticalLineToRelative(4f)
                    arcToRelative(1f, 1f, 0f, false, true, -1.993f, 0.117f)
                    lineToRelative(-0.007f, -0.117f)
                    verticalLineToRelative(-4f)
                    horizontalLineToRelative(-4f)
                    arcToRelative(1f, 1f, 0f, false, true, -0.993f, -0.883f)
                    lineToRelative(-0.007f, -0.117f)
                    verticalLineToRelative(-2f)
                    arcToRelative(1f, 1f, 0f, false, true, 0.06f, -0.34f)
                    lineToRelative(0.046f, -0.107f)
                    lineToRelative(1.894f, -3.791f)
                    verticalLineToRelative(-4.762f)
                    arcToRelative(1f, 1f, 0f, false, true, -0.117f, -1.993f)
                    lineToRelative(0.117f, -0.007f)
                    horizontalLineToRelative(8f)
                    close()
                }
            }.build()

            return _TablerPinned!!
        }

    private var _TablerPinned: ImageVector? = null



}