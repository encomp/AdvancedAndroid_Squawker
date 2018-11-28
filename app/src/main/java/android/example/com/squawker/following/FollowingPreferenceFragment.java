/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.example.com.squawker.following;

import android.content.SharedPreferences;
import android.example.com.squawker.R;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

/** Shows the list of instructors you can follow */
public class FollowingPreferenceFragment extends PreferenceFragmentCompat
    implements SharedPreferences.OnSharedPreferenceChangeListener {

  private static final String LOG_TAG = FollowingPreferenceFragment.class.getSimpleName();

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.following_squawker);
    getPreferenceScreen()
        .getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(this::onSharedPreferenceChanged);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Preference preference = findPreference(key);
    if (preference != null && preference instanceof SwitchPreferenceCompat) {
      if (sharedPreferences.getBoolean(key, false)) {
        FirebaseMessaging.getInstance().subscribeToTopic(key);
        Log.d(LOG_TAG, "Subscribing to " + key);
      } else {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
        Log.d(LOG_TAG, "Un-subscribing to " + key);
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    getPreferenceScreen()
        .getSharedPreferences()
        .unregisterOnSharedPreferenceChangeListener(this::onSharedPreferenceChanged);
  }
}
