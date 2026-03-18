package com.example.videogames.ui.screens.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.videogames.R
import com.example.videogames.domain.model.GameDetails
import com.example.videogames.ui.components.ErrorState
import com.example.videogames.ui.components.FullScreenLoading
import com.example.videogames.ui.components.RatingBar
import com.example.videogames.ui.screens.gamedetails.contract.GameDetailsIntent
import com.example.videogames.ui.screens.gamedetails.contract.GameDetailsState
import com.example.videogames.ui.screens.gamedetails.logic.GameDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailsScreen(
    gameId: Int,
    onBackClick: () -> Unit,
    viewModel: GameDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.viewState.collectAsState()
    LaunchedEffect(gameId) {
        viewModel.sendIntent(GameDetailsIntent.LoadDetails(gameId))
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (val currentState=state) {
                            is GameDetailsState.Idle -> "Game Details"
                            is GameDetailsState.Loaded -> currentState.game?.name ?: "Game Details"
                        },
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_revert),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val gameDetailsState = state) {
                is GameDetailsState.Idle -> {
                    FullScreenLoading()
                }
                is GameDetailsState.Loaded -> {
                    when {
                        gameDetailsState.isLoading && gameDetailsState.game == null -> {
                            FullScreenLoading()
                        }
                        gameDetailsState.error != null && gameDetailsState.game == null -> {
                            ErrorState(
                                message = gameDetailsState.error,
                                onRetry = { viewModel.sendIntent(GameDetailsIntent.Retry(gameId)) }
                            )
                        }
                        gameDetailsState.game != null -> {
                            GameDetailsContent(game = gameDetailsState.game!!)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GameDetailsContent(
    game: GameDetails,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = game.imageUrl,
            contentDescription = game.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = game.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatingBar(rating = game.rating)

                if (game.releaseDate.isNotBlank()) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_my_calendar),
                        contentDescription = "Release date",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = game.releaseDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (game.genres.isNotEmpty()) {

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Genres",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    game.genres.forEach { genre ->
                        AssistChip(
                            onClick = { },
                            label = { Text(genre) }
                        )
                    }
                }
            }

            // Platforms
            if (game.platforms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Platforms",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = game.platforms.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Publishers
            if (game.publishers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.publishers),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = game.publishers.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Description
            if (game.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.about),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

