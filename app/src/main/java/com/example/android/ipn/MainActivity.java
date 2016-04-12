package com.example.android.ipn;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    //Quantity digits in TIN
    final static Integer IPN_NUM = 10;

    //Color for Fake TIN
    String ipnFakeColorText = "#FF3838";
    String ipnFakeColorBackground = "#FFD7D7";

    //Color for Real TIN
    String ipnRealColorText = "#4CAF50";
    String ipnRealColorBackground = "#C8E6C9";

    //Birthday Date
    private Date  birhDayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /**
     * This method will be call from Layout activity_main
     * This method do all procedure for Decoding and Displaying Results
     *
     * @param view View
     */
    public void decodeNumber(View view) {

        EditText inputNumber = (EditText) findViewById(R.id.ipn_input);
        ImageView secretLogo = (ImageView) findViewById(R.id.secret_logo);

        String ipn = inputNumber.getText().toString();

        int ipnLength = ipn.length();

        if (ipnLength == IPN_NUM) {

            String[] ipnNumbers = ipn.split("");

            Integer birthDayCode = Integer.parseInt(ipn.substring(0, 5));


            Integer sexCode = Integer.parseInt(ipn.substring(8, 9));

            String birthdayDate = calculateBirthDay(birthDayCode);
            String sex = calculateSex(sexCode);
            String fakeStatus = checkFake(ipnNumbers);

            displayInformation(birthdayDate, sex, fakeStatus);


            secretLogo.setImageResource(R.drawable.wiki_logo);


        } else {


            Context context = getApplicationContext();

            int numbersDiverge = IPN_NUM - ipnLength;

            CharSequence text = getString(R.string.ipn_numbers_less) + numbersDiverge;

            if (ipnLength == 0) text = getString(R.string.ipn_numbers_no);

            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();


        }


    }

    /**
     * Method checks is TIN real or fake
     * @param ipnNumbers TIN from Input
     * @return Text Result with color style
     */
    private String checkFake(String[] ipnNumbers) {

        Integer[] ipnInt = new Integer[10];

        int i = 0;
        for (String ipnNumber : ipnNumbers) {

            if (!ipnNumber.isEmpty()) {
                ipnInt[i] = Integer.parseInt(ipnNumber);
                i++;
            }

        }

        Integer controlSum = (ipnInt[0] * (-1)) + (ipnInt[1] * 5) + (ipnInt[2] * 7) +
                (ipnInt[3] * 9) + (ipnInt[4] * 4) + (ipnInt[5] * 6) + (ipnInt[6] * 10) +
                (ipnInt[7] * 5) + (ipnInt[8] * 7);

        String dValue = "" + controlSum / 11;
        String[] intValue = dValue.split("(\\.)");

        Integer finControlSum = Integer.parseInt(intValue[0]) * 11;
        Integer controlNumber = controlSum - finControlSum;

        TextView statusIpnView = (TextView) findViewById(R.id.ipn_result);

        if (controlNumber == ipnInt[9]) {

            statusIpnView.setTextColor(Color.parseColor(ipnRealColorText));
            statusIpnView.setBackgroundColor(Color.parseColor(ipnRealColorBackground));

            return getString(R.string.ipn_not_fake);

        } else {

            statusIpnView.setTextColor(Color.parseColor(ipnFakeColorText));
            statusIpnView.setBackgroundColor(Color.parseColor(ipnFakeColorBackground));

            return getString(R.string.ipn_fake);

        }
    }

    /**
     * Calculate Sex depending from special number,
     * if number  odd - it's Man, if even - it's Woman
     *
     * @param sexCode Integer value should be ninth symbol in TIN
     * @return String Message Woman or Man
     */
    private String calculateSex(Integer sexCode) {

        Boolean sex = (sexCode % 2 != 0);

        if (sex) {
            return getString(R.string.ipn_sex_man);
        } else {
            return getString(R.string.ipn_sex_woman);

        }

    }

    /**
     * This function takes first five digits from TIN and return TIN owner's Birthday Date
     *
     * @param birthDayCode Integer 5 digits from Start of TIN number
     * @return String formatted Date
     */
    private String calculateBirthDay(Integer birthDayCode) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();


        try {
            Date startDate = (Date) formatter.parse("31-12-1899");
            c.setTime(startDate);


        } catch (Exception e) {

            return "";

        }
        c.add(Calendar.DATE, birthDayCode);

        birhDayDate = c.getTime();
        return  formatter.format(birhDayDate);

    }


    /**
     * Display information to the View in Layout activity_main
     * @param birthday   String Birthday Date
     * @param sex        String Sex
     * @param fakeStatus String Fake or Real TIN
     */
    private void displayInformation(String birthday, String sex, String fakeStatus) {
        TextView resultSection = (TextView) findViewById(R.id.ipn_result);

        String outputResult = birthday + "\n" + sex + "\n" + fakeStatus;

        resultSection.setText(outputResult);


    }

    /**
     * Create Intent open Browser and will be looking in Wikipedia who have born in same day before
     *
     *
     * @param view View
     */

    public void openLink(View view){


        if(birhDayDate == null){

            return;
        }

        SimpleDateFormat  wikiFormat = new SimpleDateFormat("MMMM_d");

        String birhDayWiki=wikiFormat.format(birhDayDate);

        Intent sendToBrowser = new Intent();
        String url="https://en.m.wikipedia.org/wiki/"+birhDayWiki+"#Births";

        Uri webPage = Uri.parse(url);
        sendToBrowser.setAction(Intent.ACTION_VIEW);
        sendToBrowser.setData(webPage);


        if (sendToBrowser.resolveActivity(getPackageManager()) != null) {
            startActivity(sendToBrowser);
        }
    }



}
