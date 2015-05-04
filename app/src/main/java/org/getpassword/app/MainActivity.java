package org.getpassword.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    static int pwdlen_min = 4;
    static int pwdlen_max = 15;

    static final String PREFS_NAME = "GetPassword.conf";

    static String pat_symbs = "!@#$%^&*?()";
    static String pat_numbs = "0123456789";
    static String pat_lowerchars = "abcdefghjkmnpqrstuvwxyz";
    static String pat_lowerchars_sim = "ilo";
    static String pat_upperchars = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    static String pat_upperchars_sim = "IO";
    static String pat_amb = "{}[]`~\"'\\|/<>:;,.-_=";

    boolean passwd_was_gen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.smartphone) != null) this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button pwdgen_do = (Button)findViewById(R.id.pwdgen_do);
        pwdgen_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gen_passwd();
            }
        });

        EditText pwdgen_passwd = (EditText)findViewById(R.id.pwdgen_passwd);
        pwdgen_passwd.setOnClickListener(new DoubleClickListener() {

            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {
                if (passwd_was_gen) {
                    EditText pwdgen_passwd = (EditText) findViewById(R.id.pwdgen_passwd);
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("simple text", pwdgen_passwd.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.err_was_copied), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.err_cant_copy), Toast.LENGTH_SHORT).show();
                }
            }
        });

        SeekBar pwdgen_len_bar = (SeekBar)findViewById(R.id.pwdgen_len_bar);
        pwdgen_len_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int r = (int)Math.round((double)progress / ((double)100 / (double)(pwdlen_max - pwdlen_min))) + pwdlen_min;
                TextView pwdgen_len = (TextView)findViewById(R.id.pwdgen_len);
                pwdgen_len.setText(String.valueOf(r));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        CheckBox chk_incl_symbols = (CheckBox)findViewById(R.id.chk_incl_symbols);
        CheckBox chk_incl_numbers = (CheckBox)findViewById(R.id.chk_incl_numbers);
        CheckBox chk_incl_lowerchars = (CheckBox)findViewById(R.id.chk_incl_lowerchars);
        CheckBox chk_incl_upperchars = (CheckBox)findViewById(R.id.chk_incl_upperchars);
        CheckBox chk_excl_simchars = (CheckBox)findViewById(R.id.chk_excl_simchars);
        CheckBox chk_excl_ambchars = (CheckBox)findViewById(R.id.chk_excl_ambchars);
        CheckBox chk_autocopy = (CheckBox)findViewById(R.id.chk_autocopy);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        upd_passwd_len(pwdlen_max);
        upd_passwd_len(settings.getInt("passwd_len", 10));

        chk_incl_symbols.setChecked(settings.getBoolean("chk_incl_symbols", true));
        chk_incl_numbers.setChecked(settings.getBoolean("chk_incl_numbers", true));
        chk_incl_lowerchars.setChecked(settings.getBoolean("chk_incl_lowerchars", true));
        chk_incl_upperchars.setChecked(settings.getBoolean("chk_incl_upperchars", true));
        chk_excl_simchars.setChecked(settings.getBoolean("chk_excl_simchars", false));
        chk_excl_ambchars.setChecked(settings.getBoolean("chk_excl_ambchars", true));
        chk_autocopy.setChecked(settings.getBoolean("chk_autocopy", false));
    }

    @Override
    public void onPause() {
        passwd_was_gen = false;
        EditText pwdgen_passwd = (EditText)findViewById(R.id.pwdgen_passwd);
        pwdgen_passwd.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        pwdgen_passwd.setText(getResources().getText(R.string.main_newpwd));

        super.onPause();
    }

    @Override
    protected void onStop() {
        SeekBar pwdgen_len_bar = (SeekBar)findViewById(R.id.pwdgen_len_bar);
        CheckBox chk_incl_symbols = (CheckBox)findViewById(R.id.chk_incl_symbols);
        CheckBox chk_incl_numbers = (CheckBox)findViewById(R.id.chk_incl_numbers);
        CheckBox chk_incl_lowerchars = (CheckBox)findViewById(R.id.chk_incl_lowerchars);
        CheckBox chk_incl_upperchars = (CheckBox)findViewById(R.id.chk_incl_upperchars);
        CheckBox chk_excl_simchars = (CheckBox)findViewById(R.id.chk_excl_simchars);
        CheckBox chk_excl_ambchars = (CheckBox)findViewById(R.id.chk_excl_ambchars);
        CheckBox chk_autocopy = (CheckBox)findViewById(R.id.chk_autocopy);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        int p_len = (int) Math.round((double) pwdgen_len_bar.getProgress() / ((double) 100 / (double) (pwdlen_max - pwdlen_min))) + pwdlen_min;

        editor.putInt("passwd_len", p_len);
        editor.putBoolean("chk_incl_symbols", chk_incl_symbols.isChecked());
        editor.putBoolean("chk_incl_numbers", chk_incl_numbers.isChecked());
        editor.putBoolean("chk_incl_lowerchars", chk_incl_lowerchars.isChecked());
        editor.putBoolean("chk_incl_upperchars", chk_incl_upperchars.isChecked());
        editor.putBoolean("chk_excl_simchars", chk_excl_simchars.isChecked());
        editor.putBoolean("chk_excl_ambchars", chk_excl_ambchars.isChecked());
        editor.putBoolean("chk_autocopy", chk_autocopy.isChecked());
        editor.commit();

        super.onStop();
    }

    void gen_passwd() {
        passwd_was_gen = false;

        EditText pwdgen_passwd = (EditText) findViewById(R.id.pwdgen_passwd);
        SeekBar pwdgen_len_bar = (SeekBar) findViewById(R.id.pwdgen_len_bar);
        CheckBox chk_incl_symbols = (CheckBox) findViewById(R.id.chk_incl_symbols);
        CheckBox chk_incl_numbers = (CheckBox) findViewById(R.id.chk_incl_numbers);
        CheckBox chk_incl_lowerchars = (CheckBox) findViewById(R.id.chk_incl_lowerchars);
        CheckBox chk_incl_upperchars = (CheckBox) findViewById(R.id.chk_incl_upperchars);
        CheckBox chk_excl_simchars = (CheckBox) findViewById(R.id.chk_excl_simchars);
        CheckBox chk_excl_ambchars = (CheckBox) findViewById(R.id.chk_excl_ambchars);
        CheckBox chk_autocopy = (CheckBox) findViewById(R.id.chk_autocopy);

        int p_len = (int) Math.round((double) pwdgen_len_bar.getProgress() / ((double) 100 / (double) (pwdlen_max - pwdlen_min))) + pwdlen_min;
        String pat_final = "";
        if (chk_incl_symbols.isChecked()) {
            pat_final += pat_symbs;
            if (!chk_excl_ambchars.isChecked()) pat_final += pat_amb;
        }
        if (chk_incl_numbers.isChecked()) pat_final += pat_numbs;
        if (chk_incl_lowerchars.isChecked()) {
            pat_final += pat_lowerchars;
            if (!chk_excl_simchars.isChecked()) pat_final += pat_lowerchars_sim;
        }

        if (chk_incl_upperchars.isChecked()) {
            pat_final += pat_upperchars;
            if (!chk_excl_simchars.isChecked()) pat_final += pat_upperchars_sim;
        }

        for (int i = 0; i < 10; i++) pat_final += pat_final;
        int pat_len = pat_final.length();
        if (pat_len == 0) {
            pwdgen_passwd.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            pwdgen_passwd.setText(getResources().getText(R.string.err_nothing_selected));
            return;
        }

        String p_result = "";
        for (int i = 0; i < p_len; i++) {
            double x1 = Math.random();
            int x2 = (int)((x1 * 1000000) % pat_len);
            p_result += pat_final.substring(x2, x2 + 1);
        }
        pwdgen_passwd.setText(p_result);
        pwdgen_passwd.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);

        if (chk_autocopy.isChecked()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("simple text", pwdgen_passwd.getText());
            clipboard.setPrimaryClip(clip);
        }

        passwd_was_gen = true;
    }

    void upd_passwd_len(int l) {
        SeekBar pwdgen_len_bar = (SeekBar)findViewById(R.id.pwdgen_len_bar);
        int pr = (int)Math.round(((double)100 / (double)(pwdlen_max - pwdlen_min)) * (double)(l - pwdlen_min));
        pwdgen_len_bar.setProgress(pr);
    }

}
