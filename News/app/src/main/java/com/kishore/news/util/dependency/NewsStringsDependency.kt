package com.kishore.news.util.dependency

import javax.inject.Inject

class NewsStringsDependency @Inject constructor() {

    @CountryCode
    @Inject
    lateinit var countryCode : String

    @Country
    @Inject
    lateinit var country : String
}