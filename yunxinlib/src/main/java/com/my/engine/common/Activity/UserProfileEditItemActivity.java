package com.my.engine.common.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.my.engine.common.R;
import com.my.engine.common.clazz.UserConstant;
import com.my.engine.common.clazz.UserUpdateHelper;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProfileEditItemActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_KEY = "EXTRA_KEY";
    private static final String EXTRA_DATA = "EXTRA_DATA";
    public static final int REQUEST_CODE = 1000;

    // data
    private int key;
    private String data;
    private int birthYear = 1990;
    private int birthMonth = 10;
    private int birthDay = 10;
    private Map<Integer, UserInfoFieldEnum> fieldMap;

    // VIEW
    private ClearableEditTextWithIcon editText;
    private ImageView image_finish;
    //action_bar_right_clickable_textview
    // gender layout
    private RelativeLayout maleLayout;
    private RelativeLayout femaleLayout;
    private RelativeLayout otherLayout;
    private ImageView maleCheck;
    private ImageView femaleCheck;
    private ImageView otherCheck;

    // birth layout
    private RelativeLayout birthPickerLayout;
    private TextView birthText;
    private int gender;
    private TextView textView;

    public static final void startActivity(Context context, int key, String data) {
        Intent intent = new Intent();
        intent.setClass(context, UserProfileEditItemActivity.class);
        intent.putExtra(EXTRA_KEY, key);
        intent.putExtra(EXTRA_DATA, data);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        if (key == UserConstant.KEY_NICKNAME || key == UserConstant.KEY_PHONE || key == UserConstant.KEY_EMAIL
                || key == UserConstant.KEY_SIGNATURE || key == UserConstant.KEY_ALIAS) {
            setContentView(R.layout.user_profile_edittext_layout);
            findEditText();
        } else if (key == UserConstant.KEY_GENDER) {
            setContentView(R.layout.user_profile_gender_layout);
            findGenderViews();
        } else if (key == UserConstant.KEY_BIRTH) {
            setContentView(R.layout.user_profile_birth_layout);
            findBirthViews();
        }
        initActionbar();
        setTitles();
    }

    @Override
    public void onBackPressed() {
        showKeyboard(false);
        super.onBackPressed();
    }

    private void parseIntent() {
        key = getIntent().getIntExtra(EXTRA_KEY, -1);
        data = getIntent().getStringExtra(EXTRA_DATA);
    }

    private void setTitles() {
        textView = findViewById(R.id.text);
        switch (key) {
            case UserConstant.KEY_NICKNAME:
                textView.setText(R.string.nickname);
                break;
            case UserConstant.KEY_PHONE:
                textView.setText(R.string.phone_number);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case UserConstant.KEY_EMAIL:
                textView.setText(R.string.email);
                break;
            case UserConstant.KEY_SIGNATURE:
                textView.setText(R.string.signature);
                break;
            case UserConstant.KEY_GENDER:
                textView.setText(R.string.gender);
                break;
            case UserConstant.KEY_BIRTH:
                textView.setText(R.string.birthday);
                break;
            case UserConstant.KEY_ALIAS:
                textView.setText(R.string.alias);
                break;
        }
    }

    private void findEditText() {
        editText = findViewById(R.id.edittext);
        image_finish = findViewById(R.id.image_finish);
        image_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (key == UserConstant.KEY_NICKNAME) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        } else if (key == UserConstant.KEY_PHONE) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
        } else if (key == UserConstant.KEY_EMAIL || key == UserConstant.KEY_SIGNATURE) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        } else if (key == UserConstant.KEY_ALIAS) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        }
        if (key == UserConstant.KEY_ALIAS) {
            String alias = NimUIKit.getContactProvider().getAlias(data);
            if (!TextUtils.isEmpty(alias)) {
                editText.setText(alias);
            } else {
                editText.setHint("请输入备注名...");
            }
        } else {
            editText.setText(data);
        }
        editText.setDeleteImage(R.drawable.nim_grey_delete_icon);
    }

    private void initActionbar() {//
        TextView toolbarView = findViewById(R.id.action_bar_right_clickable_textview);
        toolbarView.setText("保存");
        toolbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetAvailable(UserProfileEditItemActivity.this)) {
                    Toast.makeText(UserProfileEditItemActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (key == UserConstant.KEY_NICKNAME && TextUtils.isEmpty(editText.getText().toString().trim())) {
                    Toast.makeText(UserProfileEditItemActivity.this, R.string.nickname_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (key == UserConstant.KEY_BIRTH) {
                    update(birthText.getText().toString());
                } else if (key == UserConstant.KEY_GENDER) {
                    update(Integer.valueOf(gender));
                } else {
                    update(editText.getText().toString().trim());
                }
            }
        });
    }

    private void findGenderViews() {
        image_finish = findViewById(R.id.image_finish);
        image_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        maleLayout = findViewById(R.id.male_layout);
        femaleLayout = findViewById(R.id.female_layout);
        otherLayout = findViewById(R.id.other_layout);

        maleCheck = findViewById(R.id.male_check);
        femaleCheck = findViewById(R.id.female_check);
        otherCheck = findViewById(R.id.other_check);

        maleLayout.setOnClickListener(this);
        femaleLayout.setOnClickListener(this);
        otherLayout.setOnClickListener(this);

        initGender();
    }

    private void initGender() {
        gender = Integer.parseInt(data);
        genderCheck(gender);
    }

    private void findBirthViews() {
        image_finish = findViewById(R.id.image_finish);
        image_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        birthPickerLayout = findViewById(R.id.birth_picker_layout);
        birthText = findViewById(R.id.birth_value);

        birthPickerLayout.setOnClickListener(this);
        birthText.setText(data);

        if (!TextUtils.isEmpty(data)) {
            Date date = TimeUtil.getDateFromFormatString(data);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (date != null) {
                birthYear = cal.get(Calendar.YEAR);
                birthMonth = cal.get(Calendar.MONTH);
                birthDay = cal.get(Calendar.DATE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.male_layout) {
            gender = GenderEnum.MALE.getValue();
            genderCheck(gender);
        } else if (v.getId() == R.id.female_layout) {
            gender = GenderEnum.FEMALE.getValue();
            genderCheck(gender);
        } else if (v.getId() == R.id.other_layout) {
            gender = GenderEnum.UNKNOWN.getValue();
            genderCheck(gender);
        } else if (v.getId() == R.id.birth_picker_layout) {
            openTimePicker();
        }
    }

    private void genderCheck(int selected) {
        otherCheck.setBackgroundResource(selected == GenderEnum.UNKNOWN.getValue() ? R.drawable.nim_contact_checkbox_checked_green : R.drawable.nim_checkbox_not_checked);
        maleCheck.setBackgroundResource(selected == GenderEnum.MALE.getValue() ? R.drawable.nim_contact_checkbox_checked_green : R.drawable.nim_checkbox_not_checked);
        femaleCheck.setBackgroundResource(selected == GenderEnum.FEMALE.getValue() ? R.drawable.nim_contact_checkbox_checked_green : R.drawable.nim_checkbox_not_checked);
    }

    private void openTimePicker() {
        MyDatePickerDialog datePickerDialog = new MyDatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                birthYear = year;
                birthMonth = monthOfYear;
                birthDay = dayOfMonth;
                updateDate();

            }
        }, birthYear, birthMonth, birthDay);
        datePickerDialog.show();
    }

    private void updateDate() {
        birthText.setText(TimeUtil.getFormatDatetime(birthYear, birthMonth, birthDay));
    }

    private void update(Serializable content) {
        RequestCallbackWrapper callback = new RequestCallbackWrapper() {
            @Override
            public void onResult(int code, Object result, Throwable exception) {
                DialogMaker.dismissProgressDialog();
                if (code == ResponseCode.RES_SUCCESS) {
                    onUpdateCompleted();
                } else if (code == ResponseCode.RES_ETIMEOUT) {
                    Toast.makeText(UserProfileEditItemActivity.this, R.string.user_info_update_failed, Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (key == UserConstant.KEY_ALIAS) {
            DialogMaker.showProgressDialog(this, null, true);
            Map<FriendFieldEnum, Object> map = new HashMap<>();
            map.put(FriendFieldEnum.ALIAS, content);
            NIMClient.getService(FriendService.class).updateFriendFields(data, map).setCallback(callback);
        } else {
            if (fieldMap == null) {
                fieldMap = new HashMap<>();
                fieldMap.put(UserConstant.KEY_NICKNAME, UserInfoFieldEnum.Name);
                fieldMap.put(UserConstant.KEY_PHONE, UserInfoFieldEnum.MOBILE);
                fieldMap.put(UserConstant.KEY_SIGNATURE, UserInfoFieldEnum.SIGNATURE);
                fieldMap.put(UserConstant.KEY_EMAIL, UserInfoFieldEnum.EMAIL);
                fieldMap.put(UserConstant.KEY_BIRTH, UserInfoFieldEnum.BIRTHDAY);
                fieldMap.put(UserConstant.KEY_GENDER, UserInfoFieldEnum.GENDER);
            }
            DialogMaker.showProgressDialog(this, null, true);
            UserUpdateHelper.update(fieldMap.get(key), content, callback);
        }
    }

    private void onUpdateCompleted() {
        showKeyboard(false);
        Toast.makeText(UserProfileEditItemActivity.this, R.string.user_info_update_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    private class MyDatePickerDialog extends DatePickerDialog {
        private int maxYear = 2015;
        private int minYear = 1900;
        private int currYear;
        private int currMonthOfYear;
        private int currDayOfMonth;

        public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
            currYear = year;
            currMonthOfYear = monthOfYear;
            currDayOfMonth = dayOfMonth;
        }

        @Override
        public void onDateChanged(DatePicker view, int year, int month, int day) {
            if (year >= minYear && year <= maxYear) {
                currYear = year;
                currMonthOfYear = month;
                currDayOfMonth = day;
            } else {
                if (currYear > maxYear) {
                    currYear = maxYear;
                } else if (currYear < minYear) {
                    currYear = minYear;
                }
                updateDate(currYear, currMonthOfYear, currDayOfMonth);
            }
        }

        public void setMaxYear(int year) {
            maxYear = year;
        }

        public void setMinYear(int year) {
            minYear = year;
        }

        public void setTitle(CharSequence title) {
            super.setTitle("生 日");
        }
    }

    protected void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            if (getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(getCurrentFocus(), 0);
            }
        } else {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
