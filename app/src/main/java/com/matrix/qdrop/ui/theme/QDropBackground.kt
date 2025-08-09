import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.matrix.qdrop.R

@Composable
fun QDropBackground() {
    Image(
        painter = painterResource(id = R.drawable.background_bokeh),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 0.25f }
            .blur(80.dp),
        contentScale = ContentScale.Crop
    )
}
