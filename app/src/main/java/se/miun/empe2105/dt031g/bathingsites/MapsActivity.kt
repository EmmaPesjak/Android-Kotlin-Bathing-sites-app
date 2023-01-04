package se.miun.empe2105.dt031g.bathingsites

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import se.miun.empe2105.dt031g.bathingsites.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    //https://www.youtube.com/watch?v=PtZe-0aJ4f0 nä den blev inte bra
    //https://www.youtube.com/watch?v=FotQIcC91V4 denna fungerade ish

    //https://developers.google.com/codelabs/maps-platform/maps-platform-101-android#5 mycket från denna

    private lateinit var currentLocation : Location
    private lateinit var lastLocation : Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    //private val permissionCode = 101

    private lateinit var appDatabase: AppDatabase

    //vet inte varför detta är i ett companion obj
    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)



        appDatabase = AppDatabase.getDatabase(this)


        //getCurrentLocationUser()
    }

//    private fun getCurrentLocationUser() {
//        if (ActivityCompat.checkSelfPermission(
//                this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
//                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                    PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(
//                            this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                        permissionCode)
//            return
//        }
//
//        val getLocation = fusedLocationProviderClient.lastLocation.addOnSuccessListener {
//
//            location ->
//
//            if(location != null) {
//
//                println(location.latitude)
//                currentLocation = location
//
//                Toast.makeText(applicationContext, currentLocation.latitude.toString() + " " +
//                currentLocation.longitude.toString(), Toast.LENGTH_LONG).show()
//
//                val mapFragment = supportFragmentManager
//                    .findFragmentById(R.id.map) as SupportMapFragment
//                mapFragment.getMapAsync(this)
//            }
//        }
//    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        when (requestCode) {
//
//            permissionCode -> if(grantResults.isNotEmpty() && grantResults[0] ==
//                    PackageManager.PERMISSION_GRANTED) {
//                getCurrentLocationUser()
//            }
//        }
//    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//
//        // Add a marker in Sydney and move the camera
//        val tormestorp = LatLng(56.1111, 13.7431)  // Awesome people live here.
//        mMap.addMarker(MarkerOptions().position(tormestorp).title("Marker in Tormestorp"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(tormestorp))


        //Detta är bara för att det ska fungera, hämtar inte current location...
//        val location = Location("")
//        location.longitude= 13.7431
//        location.latitude = 56.1111
//        currentLocation = location


        mMap.uiSettings.isZoomControlsEnabled = true


        mMap.setOnMarkerClickListener(this)
        setUpMap()


//        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
//        val markerOptions = MarkerOptions().position(latLng).title("Current location")
//
//        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7f))
//        mMap.addMarker(markerOptions)
    }


    private fun setUpMap() {

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

//            && ActivityCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE
            )

            return
        }
        mMap.isMyLocationEnabled = true


        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) {

            if (it != null) {
                lastLocation = it
                val currentLatLong = LatLng(it.latitude, it.longitude)
                placeMarkerOnMap(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 8f))
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {

        //här sätts en marker på current location men det behövs ju inte...
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        mMap.addMarker(markerOptions)



        // Add circle får väl in i egen fun
        var circle: Circle? = null
        circle?.remove()
        circle = mMap.addCircle(
            CircleOptions()
                .center(currentLatLong)
                .radius(50000.0)  //här ska ju settingsvärdet in
                .fillColor(ContextCompat.getColor(this, R.color.transparent_purple))
                .strokeColor(ContextCompat.getColor(this, R.color.purple_700))
        )

        //detta får väl också in i en egen fun (hämta från db)
        readData()
    }

    /**
     * Method for reading data from the database. (Samma som i showbathingsiteactivity)
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun readData() {
        lateinit var bathingSites: List<BathingSite>
        // Reading can take time, do it in a coroutine.
        GlobalScope.launch {
            bathingSites = appDatabase.bathingSiteDao().getAllSites()


            displaySites(bathingSites)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun displaySites(bathingSites: List<BathingSite>) {

        GlobalScope.launch(Dispatchers.Main) {
            bathingSites.forEach {

                val latLng = it.latitude?.let { it1 -> it.longitude?.let { it2 -> LatLng(it1.toDouble(), it2.toDouble()) } }
                val marker = latLng?.let { it1 ->
                    MarkerOptions()
                        .title(it.name)
                        .snippet(it.address)
                        .position(it1)
                }?.let { it2 ->
                    mMap.addMarker(
                        it2
                    )
                }
            }
        }

    }


    /**
     * Inflate the overflow menu.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.maps_menu, menu)
        return true
    }

    /**
     * Set option responses.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.maps_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMarkerClick(p0: Marker) = false
}
