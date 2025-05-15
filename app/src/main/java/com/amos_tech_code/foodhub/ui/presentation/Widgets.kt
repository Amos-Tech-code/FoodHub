 package com.amos_tech_code.foodhub.ui.presentation

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.ui.presentation.feature.auth.BaseAuthViewModel
import com.amos_tech_code.foodhub.ui.theme.Primary
import kotlin.reflect.KClass
import kotlin.reflect.KType


 @SuppressLint("ContextCastToActivity")
@Composable
fun GroupSocialButtons(
    color: Color = Color.White,
    viewModel: BaseAuthViewModel
) {

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                thickness = 1.dp,
                color = color
            )
            Text(
                text = stringResource(id = R.string.sign_in_with),
                color = color,
                modifier = Modifier.padding(8.dp)
            )
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                thickness = 1.dp,
                color = color
            )
        }

        val context = LocalContext.current as ComponentActivity

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SocialButton(
                icon = R.drawable.ic_facebook,
                title = R.string.sign_with_facebook,
                onClick = { viewModel.onFacebookClicked(context) }
            )
            SocialButton(
                icon = R.drawable.ic_google,
                title = R.string.sign_with_google,
                onClick = { viewModel.onGoogleClicked(context) }
            )
        }

    }
}


@Composable
fun SocialButton(
    icon: Int,
    title: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = RoundedCornerShape(32.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier.height(38.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(id = title),
                color = Color.Black
            )
        }
    }
}

@Composable
fun BasicDialog(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = description,
            )
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(16.dp),

                ) {
                Text(
                    text = stringResource(id = R.string.ok),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }

}


@Composable
fun FoodHubTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(10.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors().copy(
        focusedIndicatorColor = Primary,
        unfocusedIndicatorColor = Color.LightGray.copy(alpha = 0.4f),
    )
) {
    Column(Modifier.padding(vertical = 8.dp)) {
        label?.let {
            Row {
                Spacer(modifier = Modifier.size(4.dp))
                it()
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange,
            modifier,
            enabled,
            readOnly,
            textStyle.copy(fontWeight = FontWeight.SemiBold),
            null,
            placeholder,
            leadingIcon,
            trailingIcon,
            prefix,
            suffix,
            supportingText,
            isError,
            visualTransformation,
            keyboardOptions,
            keyboardActions,
            singleLine,
            maxLines,
            minLines,
            interactionSource,
            shape,
            colors
        )
    }
}


fun LazyListScope.gridItems(
    count: Int,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemContent: @Composable BoxScope.(Int) -> Unit,
) {
    gridItems(
        data = List(count) { it },
        nColumns = nColumns,
        horizontalArrangement = horizontalArrangement,
        itemContent = itemContent,
    )
}

fun <T> LazyListScope.gridItems(
    data: List<T>,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable BoxScope.(T) -> Unit,
) {
    val rows = if (data.isEmpty()) 0 else 1 + (data.count() - 1) / nColumns
    items(rows) { rowIndex ->
        Row(horizontalArrangement = horizontalArrangement) {
            for (columnIndex in 0 until nColumns) {
                val itemIndex = rowIndex * nColumns + columnIndex
                if (itemIndex < data.count()) {
                    val item = data[itemIndex]
                    androidx.compose.runtime.key(key?.invoke(item)) {
                        Box(
                            modifier = Modifier.weight(1f, fill = true),
                            propagateMinConstraints = true
                        ) {
                            itemContent.invoke(this, item)
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1f, fill = true))
                }
            }
        }
    }
}


 @Composable
 fun ErrorScreen(
     modifier: Modifier = Modifier,
     message: String,
     onRetry: () -> Unit,
     icon: ImageVector = Icons.Default.Info, // Default icon
     iconTint: Color = MaterialTheme.colorScheme.error, // Optional custom tint
     showSupportAction: Boolean = false // Toggle support button
 ) {
     Column(
         modifier = modifier
             .fillMaxSize()
             .padding(24.dp),
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.Center
     ) {
         // Customizable Error Icon
         Icon(
             imageVector = icon,
             contentDescription = "Error",
             tint = iconTint,
             modifier = Modifier.size(80.dp)
         )

         Spacer(modifier = Modifier.height(16.dp))

         // Error Title
         Text(
             text = "Something Went Wrong",
             style = MaterialTheme.typography.headlineSmall,
             fontWeight = FontWeight.Bold,
             color = MaterialTheme.colorScheme.onSurface
         )

         Spacer(modifier = Modifier.height(8.dp))

         // Error Message
         Text(
             text = message,
             style = MaterialTheme.typography.bodyMedium,
             color = MaterialTheme.colorScheme.onSurfaceVariant,
             textAlign = TextAlign.Center
         )

         Spacer(modifier = Modifier.height(32.dp))

         // Retry Button
         Button(
             onClick = onRetry,
             shape = RoundedCornerShape(8.dp),
             modifier = Modifier.widthIn(min = 200.dp)
         ) {
             Text("Retry", fontWeight = FontWeight.Medium)
         }

         // Conditional Support Link
         if (showSupportAction) {
             TextButton(onClick = { /* Open support */ }) {
                 Text("Contact Support", color = MaterialTheme.colorScheme.primary)
             }
         }
     }
 }

 @Composable
 fun LoadingScreenWithText(
     text: String = "Loading..."
 ) {
     Column(
         modifier = Modifier.fillMaxSize(),
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.Center
     ) {
         CircularProgressIndicator()
         Text(text = text)
     }
 }


 @Composable
 fun EmptyState(
     modifier: Modifier = Modifier,
     title: String,
     description: String? = null,
     icon: @Composable (() -> Unit)? = null,
     action: @Composable (() -> Unit)? = null
 ) {
     Column(
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.Center,
         modifier = modifier
             .fillMaxSize()
             .padding(24.dp)
     ) {
         // Optional icon
         icon?.invoke() ?: Icon(
             imageVector = Icons.Outlined.Info,
             contentDescription = null,
             modifier = Modifier.size(64.dp),
             tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
         )

         Spacer(modifier = Modifier.height(16.dp))

         // Title
         Text(
             text = title,
             style = MaterialTheme.typography.titleMedium,
             color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f),
             textAlign = TextAlign.Center
         )

         // Optional description
         description?.let {
             Spacer(modifier = Modifier.height(8.dp))
             Text(
                 text = description,
                 style = MaterialTheme.typography.bodyMedium,
                 color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                 textAlign = TextAlign.Center
             )
         }

         // Optional action button
         action?.let {
             Spacer(modifier = Modifier.height(24.dp))
             action.invoke()
         }
     }
 }


 @Composable
fun FoodHubNavHost(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    route: KClass<*>? = null,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    enterTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
    exitTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
    popEnterTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
    popExitTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
    sizeTransform:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? =
        null,
    builder: NavGraphBuilder.() -> Unit
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        contentAlignment = contentAlignment,
        route = route,
        typeMap = typeMap,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        sizeTransform = sizeTransform,
        builder = builder
    )

}

@Composable
fun TripleOrbitAnimation() {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val rotation1 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        label = "",
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = FastOutLinearInEasing
            )
        )
    )


    val rotation2 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        label = "",
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = FastOutLinearInEasing
            )
        )
    )


    val rotation3 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        label = "",
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutLinearInEasing
            )
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(200.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(150.dp)
                .graphicsLayer(rotationZ = rotation1.value)
                .padding(8.dp),
            strokeWidth = 5.dp,
            color = Primary
        )

        CircularProgressIndicator(
            modifier = Modifier
                .size(110.dp)
                .graphicsLayer(rotationZ = rotation2.value)
                .padding(16.dp),
            strokeWidth = 5.dp,
            color = Primary
        )

        CircularProgressIndicator(
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer(rotationZ = rotation3.value)
                .padding(24.dp),
            strokeWidth = 5.dp,
            color = Primary
        )
    }

}