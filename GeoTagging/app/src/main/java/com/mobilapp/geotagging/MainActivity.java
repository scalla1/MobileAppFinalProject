/*
Mobile App Development I -- COMP.4630 Honor Statement
The practice of good ethical behavior is essential for
maintaining good order in the classroom, providing an
enriching learning experience for students, and
training as a practicing computing professional upon
graduation. This practice is manifested in the
University's Academic Integrity policy. Students are
expected to strictly avoid academic dishonesty and
adhere to the Academic Integrity policy as outlined in
the course catalog. Violations will be dealt with as
outlined therein. All programming assignments in this
class are to be done by the student alone unless
otherwise specified. No outside help is permitted
except the instructor and approved tutors.
I certify that the work submitted with this assignment
is mine and was generated in a manner consistent with
this document, the course academic policy on the
course website on Blackboard, and the UMass Lowell
academic code.
Date:
Name: luiz
*/

package com.mobilapp.geotagging;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends AppCompatActivity {

    // Google's API for location services
    //FusedLocationProviderClient fusedLocationProviderClient;

    // Location request
    //LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}