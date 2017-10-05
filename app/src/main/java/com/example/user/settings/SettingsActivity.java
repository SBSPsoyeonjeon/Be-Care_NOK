package com.example.user.settings;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
public class SettingsActivity extends AppCompatPreferenceActivity
{
    public static final String KEY_ALL = "new_message_all";
    public static final String KEY_GAS = "new_message_gas";
    public static final String KEY_DOOR = "new_message_door";
    public static final String KEY_VIBRATE = "new_message_vibrate";
    public static final String KEY_RINGTONE = "new_message_ringtone";
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener()
    {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value)
        {
            String stringValue = value.toString();
            if (preference instanceof RingtonePreference)
            {
                if (TextUtils.isEmpty(stringValue))
                {
                    preference.setSummary(R.string.pref_ringtone_silent);
                } else
                {
                    Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));
                    if (ringtone == null)
                    {
                        preference.setSummary(null);
                    } else
                    {
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }
            } else
            {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
    private static void bindPreferenceSummaryToValue(Preference preference)
    {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_notification);
        setupActionBar();
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);
    }
    private void setupActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }
    protected boolean isValidFragment(String fragmentName)
    {
        return NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }
    public static class NotificationPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("new_message_ringtone"));
            bindPreferenceSummaryToValue(findPreference("new_message_vibrate"));
            bindPreferenceSummaryToValue(findPreference("new_message_gas"));
            bindPreferenceSummaryToValue(findPreference("new_message_door"));
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), MainActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}