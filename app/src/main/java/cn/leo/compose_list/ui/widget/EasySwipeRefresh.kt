@file:Suppress("unused")

package cn.leo.compose_list.ui.widget

import androidx.annotation.FloatRange
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @author : ling luo
 * @date : 2022/9/28
 * @description : 下拉刷新，上拉加载库
 */

//常规状态
const val NORMAL = 0

//下拉刷新状态
const val REFRESHING = 1

//上拉加载状态
const val LOADING_MORE = 2

private const val NONE = 0

private const val TOP = 1 //indicator在顶部

private const val BOTTOM = 2 //indicator在底部

private const val DRAG_MULTIPLIER = 0.5f

@Immutable
data class SwipeProgress(
    val location: Int = NONE,//是在顶部还是在底部
    val offset: Float = 0f,//可见indicator的高度
    @FloatRange(from = 0.0, to = 1.0)
    val fraction: Float = 0f //0到1，0： indicator不可见   1：可见indicator的最大高度
)

@Stable
class SwipeRefreshState(
    loadState: Int,
) {

    private val _indicatorOffset = Animatable(0f)
    private val mutatorMutex = MutatorMutex()

    var loadState: Int by mutableStateOf(loadState)

    var isSwipeInProgress: Boolean by mutableStateOf(false)
        internal set

    //上下拉的偏移量等等
    var progress: SwipeProgress by mutableStateOf(SwipeProgress())
        internal set

    internal val indicatorOffset: Float get() = _indicatorOffset.value

    internal suspend fun animateOffsetTo(offset: Float, ) {
        mutatorMutex.mutate {
            _indicatorOffset.animateTo(offset)
        }
    }

    internal suspend fun dispatchScrollDelta(
        delta: Float,
        location: Int,
        maxOffsetY: Float,
    ) {
        mutatorMutex.mutate(MutatePriority.UserInput) {
            _indicatorOffset.snapTo(_indicatorOffset.value + delta)
            updateProgress(
                location = location,
                maxOffsetY = maxOffsetY,
            )
        }
    }

    /**
     * 更新progress
     * @param maxOffsetY  下拉或者上拉indicator最大高度
     */
    private fun updateProgress(
        offsetY: Float = abs(indicatorOffset),
        location: Int,
        maxOffsetY: Float,
    ) {
        val offsetPercent = min(1f, offsetY / maxOffsetY)
        val offset = min(maxOffsetY, offsetY)
        progress = SwipeProgress(location, offset, offsetPercent)
    }
}

private class SwipeRefreshNestedScrollConnection(
    private val state: SwipeRefreshState,
    private val coroutineScope: CoroutineScope,
    private val onRefresh: () -> Unit,
    private val onLoadMore: () -> Unit
) : NestedScrollConnection {
    var refreshEnabled: Boolean = false//是否开启下拉刷新
    var loadMoreEnabled: Boolean = false//是否开启上拉加载
    var refreshTrigger: Float = 120f//最大的上拉的距离
    var indicatorHeight: Float = 60f//顶部、底部下上组合项的高度

    private var isTop = false //是否是顶部的下拉
    private var isBottom = false//是否是达到

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset = when {
        //刷新和更多都禁用 则不处理
        !refreshEnabled && !loadMoreEnabled -> Offset.Zero
        //当处于刷新状态或者更多状态，不处理
        state.loadState != NORMAL -> Offset.Zero
        source == NestedScrollSource.Drag -> {
            if (available.y > 0 && isBottom) {
                onScroll(available)
            } else if (available.y < 0 && isTop) {
                onScroll(available)
            } else {
                Offset.Zero
            }
        }
        else -> Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        //刷新和更多都禁用 则不处理
        if (!refreshEnabled && !loadMoreEnabled) {
            return Offset.Zero
        }
        //当处于刷新状态或者更多状态，不处理
        else if (state.loadState != NORMAL) {
            return Offset.Zero
        } else if (source == NestedScrollSource.Drag) {
            if (available.y < 0) {
                if (!isBottom) {
                    isBottom = true
                } else {
                    return onScroll(available)
                }
            } else if (available.y > 0) {
                if (!isTop) {
                    isTop = true
                } else {
                    return onScroll(available)
                }
            }
        }
        return Offset.Zero
    }

    private fun onScroll(available: Offset): Offset {
        if (!isBottom && !isTop) {
            return Offset.Zero
        }
        if (available.y > 0 && isTop) {
            state.isSwipeInProgress = true
        } else if (available.y < 0 && isBottom) {
            state.isSwipeInProgress = true
        } else if (state.indicatorOffset.roundToInt() == 0) {
            state.isSwipeInProgress = false
        }
        val newOffset = (available.y * DRAG_MULTIPLIER + state.indicatorOffset).let {
            if (isTop) it.coerceAtLeast(0f) else it.coerceAtMost(0f)
        }
        val dragConsumed = newOffset - state.indicatorOffset

        return if (dragConsumed.absoluteValue >= 0.5f) {
            coroutineScope.launch {
                state.dispatchScrollDelta(
                    dragConsumed,
                    if (isTop) TOP else BOTTOM,
                    refreshTrigger,
                )
            }
            Offset(x = 0f, y = dragConsumed / DRAG_MULTIPLIER)
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        if (state.loadState == NORMAL && abs(state.indicatorOffset) >= indicatorHeight) {
            if (isTop) {
                onRefresh()
            } else if (isBottom) {
                onLoadMore()
            }
        }

        state.isSwipeInProgress = false

        return Velocity.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        return Velocity.Zero.also {
            isTop = false
            isBottom = false
        }
    }
}

@Composable
fun BoxScope.LoadingIndicatorSample(
    modifier: Modifier,
    state: SwipeRefreshState,
    indicatorHeight: Dp
) {
    val height = maxOf(30.dp, with(LocalDensity.current) {
        state.progress.offset.toDp()
    })
    Box(
        modifier
            .fillMaxWidth()
            .height(height), contentAlignment = Alignment.Center
    ) {
        if (state.isSwipeInProgress) {
            if (state.progress.offset <= with(LocalDensity.current) { indicatorHeight.toPx() }) {
                Text(text = if (state.progress.location == TOP) "下拉刷新" else "上拉加载更多")
            } else {
                Text(text = if (state.progress.location == TOP) "松开刷新" else "松开加载更多")
            }
        } else {
            AnimatedVisibility(
                state.loadState == REFRESHING ||
                        state.loadState == LOADING_MORE
            ) {
                //加载中
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun EasySwipeRefresh(
    modifier: Modifier = Modifier,
    state: SwipeRefreshState,
    onRefresh: () -> Unit = {},//下拉刷新回调
    onLoadMore: () -> Unit = {},//上拉加载更多回调
    refreshTriggerDistance: Dp = 120.dp,//indication可见的最大高度
    indicationHeight: Dp = 56.dp,//indication的高度
    refreshEnabled: Boolean = true,//是否支持下拉刷新
    loadMoreEnabled: Boolean = true,//是否支持上拉加载更多
    indicator: @Composable BoxScope.(
        modifier: Modifier,
        state: SwipeRefreshState,
        indicatorHeight: Dp
    ) -> Unit = { m, s, height ->
        LoadingIndicatorSample(m, s, height)
    },//顶部或者底部的Indicator
    content: @Composable (modifier: Modifier) -> Unit,
) {
    val refreshTriggerPx = with(LocalDensity.current) { refreshTriggerDistance.toPx() }
    val indicationHeightPx = with(LocalDensity.current) { indicationHeight.toPx() }

    LaunchedEffect(state.isSwipeInProgress) {
        if (!state.isSwipeInProgress) {
            state.animateOffsetTo(0f)
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val updatedOnRefresh = rememberUpdatedState(onRefresh)
    val updatedOnLoadMore = rememberUpdatedState(onLoadMore)

    val nestedScrollConnection = remember(state, coroutineScope) {
        SwipeRefreshNestedScrollConnection(
            state,
            coroutineScope,
            onRefresh = { updatedOnRefresh.value.invoke() },
            onLoadMore = { updatedOnLoadMore.value.invoke() }
        )
    }.apply {
        this.refreshEnabled = refreshEnabled
        this.loadMoreEnabled = loadMoreEnabled
        this.refreshTrigger = refreshTriggerPx
        this.indicatorHeight = indicationHeightPx
    }

    BoxWithConstraints(modifier.nestedScroll(connection = nestedScrollConnection)) {
        if (!state.isSwipeInProgress)
            LaunchedEffect((state.loadState == REFRESHING || state.loadState == LOADING_MORE)) {
                //回弹动画
                animate(
                    animationSpec = tween(durationMillis = 300),
                    initialValue = state.progress.offset,
                    targetValue = when (state.loadState) {
                        LOADING_MORE -> indicationHeightPx
                        REFRESHING -> indicationHeightPx
                        else -> 0f
                    }
                ) { value, _ ->
                    if (!state.isSwipeInProgress) {
                        state.progress = state.progress.copy(
                            offset = value,
                            fraction = min(1f, value / refreshTriggerPx)
                        )
                    }
                }
            }

        val offsetDp = with(LocalDensity.current) {
            state.progress.offset.toDp()
        }
        //子可组合项 根据state.progress来设置子可组合项的padding
        content(
            when (state.progress.location) {
                TOP -> Modifier.padding(top = offsetDp)
                BOTTOM -> Modifier.padding(bottom = offsetDp)
                else -> Modifier
            }
        )
        if (state.progress.location != NONE) {
            //顶部、底部的indicator 纵坐标跟随state.progress移动
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(refreshTriggerDistance)
                .graphicsLayer {
                    translationY =
                        if (state.progress.location == LOADING_MORE) {
                            constraints.maxHeight - state.progress.offset
                        } else {
                            state.progress.offset - refreshTriggerPx
                        }
                }
            ) {
                indicator(
                    Modifier.align(
                        if (state.progress.location == TOP)
                            Alignment.BottomStart
                        else
                            Alignment.TopStart
                    ),
                    state,
                    indicationHeight
                )
            }
        }
    }
}


@Composable
fun rememberSwipeRefreshState(
    loadState: Int
): SwipeRefreshState {
    return remember {
        SwipeRefreshState(loadState = loadState)
    }.apply {
        this.loadState = NORMAL
    }
}