package com.example.addpost;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PlacePickerActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap googleMap;
    private Marker marker;
    private EditText searchEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        // 닫기 버튼 설정
        ImageView closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> onBackPressed()); // 이전 페이지로 이동

        // 검색 EditText 설정
        searchEditText = findViewById(R.id.search_edittext);

        // 검색 버튼 설정
        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchEditText.getText().toString();
                if (!searchQuery.isEmpty()) {
                    searchLocation(searchQuery);
                } else {
                    Toast.makeText(PlacePickerActivity.this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 등록 버튼 설정
        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker != null) {
                    LatLng location = marker.getPosition();
                    getAddressFromLatLng(location.latitude, location.longitude);
                } else {
                    Toast.makeText(PlacePickerActivity.this, "장소를 선택하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 지도 초기화
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // 현재 위치를 초기 위치로 설정
        LatLng initialLocation = new LatLng(37.5665, 126.9780); // 서울을 초기 위치로 설정 (예시)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15));

        // 지도를 클릭할 때마다 마커 위치 업데이트
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                updateMarkerPosition(latLng);
            }
        });
    }

    private void updateMarkerPosition(LatLng location) {
        if (marker == null) {
            marker = googleMap.addMarker(new MarkerOptions().position(location).draggable(true));
        } else {
            marker.setPosition(location);
        }
    }

    private void searchLocation(String searchQuery) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(searchQuery, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng location = new LatLng(address.getLatitude(), address.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                updateMarkerPosition(location);
            } else {
                Toast.makeText(this, "장소를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "장소 검색 중 오류 발생", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0);

                // 주소 값을 다른 페이지로 전달
                Intent intent = new Intent();
                intent.putExtra("address", addressText);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "주소를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "주소 검색 중 오류 발생", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}

