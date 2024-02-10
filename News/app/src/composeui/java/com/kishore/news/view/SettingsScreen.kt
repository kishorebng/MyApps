package com.kishore.news.view

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.kishore.news.R
import com.kishore.news.util.NewsLogUtil
import com.kishore.news.util.NewsUtil
import com.kishore.news.util.dependency.NewsSharedPreference
import com.kishore.news.viewmodel.NewsListViewModel


@Composable
fun NewsPreferenceScreen(navController: NavController, newsSharedPreference: NewsSharedPreference) {

    val newsListViewModel = hiltViewModel<NewsListViewModel>()

    val context = LocalContext.current
    val showCountryDialog = remember { mutableStateOf(false) }

    val onshareClicked = {
        NewsUtil.shareApp(context)
    }
    val onFeedbackClicked = {
        NewsUtil.feedback(context)
    }

    val powerByClicked = {
        NewsUtil.viewSite(context, "https://newsapi.org/")
    }
    val openSourceClicked = {
        NewsUtil.viewSite(context, "https://github.com/kishorebng/MyApps")
    }

    val contactUsClicked = {
        NewsUtil.viewSite(context, "https://krisapps.blogspot.com/2022/05/news-app-android.html")
    }

    val onArrowClicked = {
        navController.navigateUp()
    }

    val onLicenceClicked = {
        context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
    }

    val keys = stringArrayResource(R.array.country_by_labels)
    val values = stringArrayResource(R.array.country_by_values)
    val countryMap: Map<String, String> =
        keys.zip(values)
            .toMap()

    val onCountryClicked = {
        showCountryDialog.value = true
    }

    if (showCountryDialog.value) {
        preferenceRadioDialog(
            stringResource(R.string.country),
            countryMap.keys.toList(),
            countryMap.entries.find {
                it.value == newsSharedPreference.getStringPreference(
                    context.getString(
                        R.string.country_key
                    ), ""
                )
            }?.key!!,
            onOptionItemSelected = {
                NewsLogUtil.i("Country is  $it")
                newsSharedPreference.putStringPreference(
                    context.getString(R.string.country_entry_key),
                    it
                )
                newsSharedPreference.putStringPreference(
                    context.getString(R.string.country_key),
                    countryMap.get(it)!!
                )
                newsListViewModel.fetchNews()
                showCountryDialog.value = false
            },
            showDialog = showCountryDialog
        )
    }


    Column {
        SettingsAppBar(onArrowClicked)
        LazyColumn {
            item {
                categorySection(stringResource(id = R.string.app_setting))
            }
            item {
                //  preferences(stringResource(id = R.string.country), "")
                preferences(
                    stringResource(id = R.string.country),
                    countryMap.entries.find {
                        it.value == newsSharedPreference.getStringPreference(
                            context.getString(R.string.country_key),
                            ""
                        )
                    }?.key!!,
                    onCountryClicked
                )
            }
            item { Divider() }
            item { categorySection(stringResource(id = R.string.general)) }
            item {
                preferences(
                    stringResource(id = R.string.share),
                    stringResource(id = R.string.share_summary),
                    onshareClicked
                )
            }
            item {
                preferences(
                    stringResource(id = R.string.powered_by),
                    stringResource(id = R.string.powered_by_summary),
                    powerByClicked
                )
            }
            item {

                preferences(
                    stringResource(id = R.string.open_source),
                    stringResource(id = R.string.open_source_summary),
                    openSourceClicked
                )
            }

            item {
                preferences(
                    stringResource(id = R.string.feedback),
                    stringResource(id = R.string.feedback_summary),
                    onFeedbackClicked
                )
            }
            item {
                preferences(
                    stringResource(id = R.string.open_source),
                    stringResource(id = R.string.open_sources_summary),
                    onLicenceClicked
                )
            }
            item {
                preferences(stringResource(id = R.string.contact), "", contactUsClicked, false)
            }

            item {
                preferences(stringResource(id = R.string.version), NewsUtil.getVersionCode(context))
            }

        }
    }
}


/*
    Default TopBar shown when search is not Clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppBar(onArrowClicked: () -> Boolean) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
        Text(
            text = "Settings",
            fontWeight = FontWeight.Bold,
            style = (MaterialTheme.typography).titleMedium
        )
    }, navigationIcon = {
        IconButton(onClick = { onArrowClicked() }) {
            Icon(Icons.Filled.ArrowBack, "backIcon")
        }
    })
}


@Composable
fun categorySection(headerText: String) {
    Text(
        text = headerText,
        color = Color.Cyan,
        textAlign = TextAlign.Left,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 14.sp
    )
}

@Composable
fun preferences(
    title: String, subtitle: String, onClick: () -> Unit = {}, summary: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
            )
            .padding(all = 16.dp),
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
        )
        if (summary) {
            Text(
                text = subtitle, style = MaterialTheme.typography.labelLarge, color = Color.Gray
            )
        }
    }
}


@Composable
fun preferenceRadioDialog(
    title: String,
    optionsList: List<String>,
    selected: String,
    onOptionItemSelected: (String) -> Unit,
    showDialog: MutableState<Boolean>
) {

    val (selectedOption, onOptionSelected) = remember { mutableStateOf(selected) }
    Dialog(
        onDismissRequest = { showDialog.value = false },
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // TITLE
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Divider(modifier = Modifier.padding(bottom = 8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .weight(weight = 1f, fill = false)
                        .padding(vertical = 16.dp)
                ) {
                    optionsList.forEach { text ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = {
                                        onOptionSelected(text)
                                        onOptionItemSelected.invoke(
                                            text
                                        )
                                    }
                                )
                                .padding(start = 8.dp)
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = {
                                    onOptionSelected(text)
                                    onOptionItemSelected.invoke(
                                        text
                                    )
                                }
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.titleSmall.merge(),
                                modifier = Modifier.padding(start = 8.dp, top = 14.dp)
                            )
                        }
                    }
                }

                // BUTTONS
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text(text = "Cancel")
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun newsPreferenceScreenPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    NewsPreferenceScreen(
        navController,
        NewsSharedPreference(PreferenceManager.getDefaultSharedPreferences(context))
    )
}

@Preview
@Composable
fun alertPreference() {
    val showCountryDialog = remember { mutableStateOf(false) }
    preferenceRadioDialog(
        stringResource(R.string.country),
        stringArrayResource(R.array.country_by_labels).toList(),
        "Japan",
        {},
        showCountryDialog
    )
}


