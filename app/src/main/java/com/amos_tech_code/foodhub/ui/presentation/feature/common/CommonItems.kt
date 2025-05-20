package com.amos_tech_code.foodhub.ui.presentation.feature.common

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.data.model.response.FoodItem


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodItemView(
    foodItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onFoodItemClick: (foodItem: FoodItem) -> Unit,
    onFavoriteClick: (foodItem: FoodItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .width(216.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Gray.copy(alpha = 0.8f),
                spotColor = Color.Gray.copy(alpha = 0.8f)
            )
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onFoodItemClick.invoke(foodItem) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(147.dp)
        ) {
            AsyncImage(
                model = foodItem.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .sharedElement(
                        state = rememberSharedContentState(key = "image/${foodItem.id}"),
                        animatedVisibilityScope
                    ),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "$${foodItem.price}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopStart)
            )
            Image(
                painter = painterResource(R.drawable.ic_heart),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(8.dp)
                    .size(40.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        onFavoriteClick.invoke(foodItem)
                    }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 8.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = "(4.5)",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.size(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "(20)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = foodItem.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = "title/${foodItem.id}"),
                        animatedVisibilityScope
                    )
            )
            Text(
                text = foodItem.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1
            )
        }


    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogOut(onLogOutClick: () -> Unit) {

    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable { showDialog = true }
            .padding(vertical = 8.dp, horizontal = 24.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
                .size(28.dp)
                .padding(8.dp),
            contentAlignment = Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = "logout"
            )
        }

        Text(
            text = "LogOut",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = { showDialog = false},
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Text(text = "Are you sure you want to log out?")
                Row(
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(onClick = { showDialog = false }) {
                        Text(text = "Cancel")
                    }
                    TextButton(onClick = { onLogOutClick() }) {
                        Text(text = "Log out")
                    }
                }
            }
        }
    }
}

