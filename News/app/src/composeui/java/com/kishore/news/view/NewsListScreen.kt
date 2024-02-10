package com.kishore.news.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kishore.news.R
import com.kishore.news.model.database.NewsTable
import com.kishore.news.util.NewsUtil
import com.kishore.news.view.theme.listheaderBG
import com.kishore.news.viewmodel.NewsListViewModel
import com.kishore.news.viewmodel.SearchViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun NewsListScreen(navController: NavController) {

    val newsListViewModel = hiltViewModel<NewsListViewModel>()
    val headlineList = newsListViewModel.headlinesnewsdata.observeAsState().value
    val newsList = newsListViewModel.newsdata.observeAsState().value

    val onSettingsTriggered = {
        navController.navigate("preferences")
    }
    Column {
        newsAppBar(newsListViewModel, onSettingsTriggered)
        listUI(headlineList, newsList)
    }
}

@Composable
fun ErrorMsg(msg: String) {
    Text(text = msg)
}


@Composable
fun listUI1(headlinesList: List<NewsTable>?, newsList: List<NewsTable>?) {
    Column {
//        DefaultAppBar(onSearchClicked = {})
        Text(
            text = stringResource(R.string.headlines),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(start = 10.dp)
        )
        // Headlines List
        headlinesListUI(headlinesList)
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(start = 10.dp)
        )
        // News List
        newsListUI(newsList)
    }
}

@Composable
fun listUI(headlinesList: List<NewsTable>?, newsList: List<NewsTable>?) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.headlines),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 10.dp)
                )
                // Headlines List
                headlinesListUI(headlinesList)
            }
        }
        item {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
        newsList?.let {
            if (it.isNotEmpty()) {
                items(newsList) {
                    it?.let {
                        newListItem(it)
                    }
                }
            } else {
                item {
                    emptyNewsList()
                }
            }
        } ?: run {
            item {
                emptyNewsList()
            }
        }

    }

}

@Composable
fun headlinesListUI(headlinesList: List<NewsTable>?) {
//    var itemsListState by remember { mutableStateOf(headlinesList) }
    val lazyListState = rememberLazyListState()
    // Headlines List
    headlinesList?.let {
        if (it.isNotEmpty()) {
            LazyRow(
                state = lazyListState
            ) {
                items(headlinesList) {
                    it?.let {
                        headlinesListItem(it)
                    }
                }
            }
            autoScroll(lazyListState, headlinesList.size)
        } else {
            emptyHeadlinesList()
        }
    } ?: run {
        emptyHeadlinesList()
    }
}


@Composable
fun emptyHeadlinesList() {
    Text(
        "No Data Available",
        fontSize = 30.sp,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
    )

}

@Composable
fun emptyNewsList() {
    Text(
        "No Data Available",
        fontSize = 30.sp,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    )

}

@Preview
@Composable
fun pHeadlinesList() {
    emptyHeadlinesList()
}

@Preview
@Composable
fun pNewsList() {
    emptyNewsList()
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun autoScroll(listState: LazyListState, size: Int) {
    val firstVisibleIndex = listState.firstVisibleItemIndex
    val scrollProgress = listState.isScrollInProgress
    val coroutineScope = rememberCoroutineScope()
    if (!scrollProgress) {
        coroutineScope.launch {
            Log.d("Kishore", "firstVisibleIndex  $firstVisibleIndex")
            delay(1500)
            if (firstVisibleIndex != (size - 1)) {
                listState.animateScrollToItem(firstVisibleIndex + 1)
            } else {
                listState.scrollToItem(0)
            }
        }
    }
}

@Composable
fun newsListUI(newsList: List<NewsTable>?) {
    // News List
    newsList?.let {
        if (it.isNotEmpty()) {
            LazyColumn {
                items(newsList) {
                    it?.let {
                        newListItem(it)
                    }
                }
            }
        } else {
            emptyNewsList()
        }
    } ?: run {
        emptyNewsList()
    }
}


@Composable
fun newsAppBar(newsListViewModel: NewsListViewModel, onSettingsTriggered: () -> Unit) {
    var searchViewState by remember { mutableStateOf(SearchViewState.CLOSED) }
    var searchText by remember { mutableStateOf("") }
    MainAppBar(searchViewState, searchText, onTextChange = {
        searchText = it
        newsListViewModel.searchNews("%$searchText%")
    }, onCloseClicked = {
        searchViewState = SearchViewState.CLOSED
    }, onSearchClicked = {
        Log.d("Kishore", "Searched clicked $it")
    }, onSearchTriggered = {
        searchViewState = SearchViewState.OPENED
    }, onSettingsTriggered = onSettingsTriggered
    )
}


@Composable
fun MainAppBar(
    searchViewState: SearchViewState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    onSettingsTriggered: () -> Unit
) {
    when (searchViewState) {
        SearchViewState.CLOSED -> {
            DefaultAppBar(
                onSearchClicked = onSearchTriggered, onSettingsTriggered = onSettingsTriggered
            )
        }

        SearchViewState.OPENED -> {
            SearchAppBar(
                text = searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked
            )
        }
    }
}

/*
    Default TopBar shown when search is not Clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar(onSearchClicked: () -> Unit, onSettingsTriggered: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(title = {
        Text(
            text = stringResource(R.string.app_name),
            fontWeight = FontWeight.Bold,
            style = (MaterialTheme.typography).titleMedium
        )
    }, actions = {
        IconButton(onClick = { onSearchClicked() }) {
            Icon(
                imageVector = Icons.Filled.Search, contentDescription = "Search Icon"
            )
        }
        // Creating Icon button for dropdown menu
        IconButton(onClick = { showMenu = !showMenu }) {
            Icon(Icons.Default.MoreVert, "")
        }

        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(
                onClick = { onSettingsTriggered() },
                text = { Text(stringResource(R.string.action_settings)) })
        }
    })
}


@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        TextField(modifier = Modifier.fillMaxWidth(), value = text, onValueChange = {
            onTextChange(it)
        }, placeholder = {
            Text(
                fontWeight = FontWeight.Bold,
                text = stringResource(id = R.string.search),
                color = Color.Gray
            )
        }, textStyle = TextStyle(
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        ), singleLine = true, leadingIcon = {
            IconButton(onClick = {
                if (text.isNotEmpty()) {
                    onTextChange("")
                } else {
                    onCloseClicked()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, contentDescription = "Search Icon"
                )
            }
        }, keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ), keyboardActions = KeyboardActions(onSearch = {
            onSearchClicked(text)
        })
//            , colors = TextFieldDefaults.textFieldColors(
//            backgroundColor = Color.Transparent,
//            cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
//        )
        )
    }
}


@Composable
fun headlinesListItem(newsTable: NewsTable) {
    val context = LocalContext.current
    // set width of screen as lazy row goes beyond the screen
    Card(modifier = Modifier
        .width(LocalConfiguration.current.screenWidthDp.dp)
        .padding(all = 10.dp)
        .clickable { NewsUtil.viewNews(newsTable.url, context) }) {
        Column(
            modifier = Modifier.padding(all = 10.dp)
        ) {
            AsyncImage(
                model = newsTable.urlToImage,
                contentDescription = "headline",
                modifier = Modifier.height(160.dp),
                error = painterResource(R.drawable.my_news),
                placeholder = painterResource(R.drawable.my_news),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = newsTable.title!!,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,

                )
            Text(
                text = newsTable.sourceName!!,
                fontSize = 10.sp,
                color = Color.Magenta,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = NewsUtil.formatDate(newsTable.publishedAt),
                fontSize = 10.sp,
                color = Color.Gray,
            )
        }
    }
}

@Composable
fun newListItem(newsTable: NewsTable) {
    val context = LocalContext.current
    Card(modifier = Modifier
        .padding(all = 10.dp)
        .fillMaxWidth()
        .clickable {
            newsTable?.let {
                it.url?.let {
                    NewsUtil.viewNews(newsTable.url, context)
                }
            }
        }) {
        Column {
            Row(
                modifier = Modifier
                    .background(listheaderBG, RectangleShape)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(all = 10.dp),
                    text = newsTable.title!!,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = (MaterialTheme.typography).titleMedium,

                    )
            }
            ConstraintLayout(
                modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp)
            ) {
                // Create references for the composables to constrain

                Log.d(
                    "Kishore",
                    "urlToImage ${newsTable.urlToImage}  description ${newsTable.description}"
                )
                val (newsImage, newsContent) = createRefs()
                AsyncImage(
                    model = newsTable.urlToImage,
                    contentDescription = "headline",
                    modifier = Modifier
                        .padding(end = 10.dp, top = 10.dp, bottom = 10.dp)
                        .width(140.dp)
                        .height(140.dp)
                        .constrainAs(newsImage) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.absoluteRight)
                        },
                    error = painterResource(R.drawable.my_news),
                    placeholder = painterResource(R.drawable.my_news),
                    contentScale = ContentScale.FillBounds
                )
                Column(modifier = Modifier.constrainAs(newsContent) {
                    start.linkTo(parent.absoluteLeft)
                    end.linkTo(newsImage.absoluteLeft)
                    top.linkTo(newsImage.top)
                    width = Dimension.fillToConstraints // fixed text cut
                }) {
                    Text(modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                        text = newsTable.description?.let { newsTable.description }
                            ?: "Link for More info")
                }

            }

            Text(
                text = newsTable.sourceName!!,
                fontSize = 10.sp,
                color = Color.Magenta,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(all = 10.dp)
            )
            Row(modifier = Modifier.padding(end = 10.dp, start = 10.dp, bottom = 10.dp)) {
                Text(text = NewsUtil.formatDate(newsTable.publishedAt), color = Color.Gray)
                Spacer(Modifier.weight(1f)) // used for moving share button to right
                Image(
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                        .clickable { NewsUtil.onShareNews(context, newsTable.url) },
                    painter = painterResource(R.drawable.ic_share_black_18dp),
                    contentDescription = "list"
                )
            }
        }
    }
}


@Composable
@Preview
fun listScreenPreview() {
    listUI(getDumpListData(1), getDumpListData(0))
}

@Composable
@Preview
fun DefaultAppBarPreview() {
    DefaultAppBar(onSearchClicked = {}, {})
}

@Composable
@Preview
fun SearchAppBarPreview() {
    SearchAppBar(text = "Some random text",
        onTextChange = {},
        onCloseClicked = {},
        onSearchClicked = {})
}

fun getDumpListData(headline: Int): List<NewsTable> {
    return listOf(
        NewsTable(
            Date(System.currentTimeMillis()),
            "",
            "North Korea's Newest Missile Appears Similar To Advanced Russian Design - NPR",
            "Experts say the missile, tested on May 4 as part of a \"strike drill,\" could be a new and destabilizing weapon on the Korean Peninsula.",
            "https://www.npr.org/2019/05/08/721135496/north-koreas-newest-missile-appears-similar-to-advanced-russian-design",
            "https://media.npr.org/assets/img/2019/05/07/rtx6udcx_wide-b260d92d10a99b0c23c47c31855bb6e3a1402c20.jpg?s=1400",
            "2019-05-08T13:12:00Z",
            "North Korea's latest short-range missile, tested on May 4, looks similar to Russia's Iskander missile. North Korea describes it as a \"tactical guided weapon.\"\r\nKCNA/Reuters\r\nNorth Korea's newest missile has a striking resemblance to an advanced Russian design… [+5899 chars]",
            "Stereogum.com",
            headline
        ), NewsTable(
            Date(System.currentTimeMillis()),
            "CAITLIN OPRYSKO",
            "Pelosi: Trump ",
            "&quot;Every single day, the president is making a case,&quot; Pelosi said.",
            "https://www.politico.com/story/2019/05/08/pelosi-trump-self-impeachment-1311038",
            "https://static.politico.com/25/04/5a776ddb42e088508c99fc618fe3/190508-nancy-pelosi-gty-773.jpg",
            "2019-05-08T13:21:00Z",
            "House Speaker Nancy Pelosi asserted that Attorney General William Barr should be held in contempt for his refusal to release an unredacted version of the Mueller report.,",
            "Npr.org",
            headline
        ), NewsTable(
            Date(System.currentTimeMillis()),
            null,
            "Vampire Weekend & HAIM Play 2 Songs On 'Fallon': Watch - Stereogum",
            "Vampire Weekend are good at their jobs. Last weekend, the band came back from a six-year absence with the lush, expansive new album Father Of The Bride, one of those rare albums that's fun to listen to and fun to argue about. And now that their machinery has …",
            "https://www.stereogum.com/2042886/watch-vampire-weekend-make-their-glorious-return-to-tv-playing-the-tonight-show-with-haim/video/",
            "https://static.stereogum.com/uploads/2019/05/Vampire-Weekend-on-Fallon-1557320153-608x402.jpg",
            "2019-05-08T13:11:00Z",
            "Vampire Weekend are good at their jobs. Last weekend, the band came back from a six-year absence with the lush, expansive new album Father Of The Bride, one of those rare albums that’s fun to listen to and fun to argue about. And now that their machinery has … [+1781 chars]",
            "Foxbusiness.com",
            headline
        ), NewsTable(
            Date(System.currentTimeMillis()),
            "Brittany De Lea",
            "Americans have 'incredible' misconceptions about Social Security - Fox Business",
            "A new survey shows that many people still don’t understand the program’s basic tenets or purpose.",
            "https://www.foxbusiness.com/personal-finance/americans-misconceptions-social-security",
            "https://a57.foxnews.com/static.foxbusiness.com/foxbusiness.com/content/uploads/2018/02/0/0/istock-494146669-1.jpg?ve=1&tl=1",
            "2019-05-08T12:50:27Z",
            "Rep. David Schweikert (R-Ariz.) on the report that Social Security will run out of money by 2035 and that Medicare will become insolvent by 2026.\r\nOlder Americans – including current and future retirees – appear to have a meaningful lack of knowledge about So… [+3096 chars]",
            "Vox.com",
            headline
        )
    )
}





