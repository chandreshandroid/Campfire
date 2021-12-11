package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.interfacee

import android.os.Bundle
import androidx.fragment.app.Fragment

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

interface NavigationHost {
    /**
     * Trigger a navigation to the specified fragment, optionally adding a transaction to the back
     * stack to make this navigation reversible.
     */

    fun navigateTo(fragment: Fragment, string: String, addToBackstack: Boolean)
    fun navigateTo(fragment: Fragment, bundle: Bundle, string:String, addToBackstack: Boolean)
}

