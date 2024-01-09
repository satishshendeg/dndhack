/*
 * Copyright 2021 The Android Open Source Project
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
package com.example.dnd.richreceiver

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.common.util.concurrent.ListeningScheduledExecutorService
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.Executor
import java.util.concurrent.Executors

internal object MyExecutors {
    private val BG = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor())
    private val MAIN_HANDLER = Handler(Looper.getMainLooper())
    private val MAIN_EXECUTOR = Executor { runnable: Runnable? ->
        if (!MAIN_HANDLER.post(
                runnable!!
            )
        ) {
            Log.e("ReceiveContentDemo", "Failed to post runnable on main thread")
        }
    }

    @JvmStatic
    fun bg(): ListeningScheduledExecutorService {
        return BG
    }

    @JvmStatic
    fun main(): Executor {
        return MAIN_EXECUTOR
    }
}