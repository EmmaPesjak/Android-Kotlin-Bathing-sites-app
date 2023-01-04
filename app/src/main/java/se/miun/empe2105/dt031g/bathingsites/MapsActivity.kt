package se.miun.empe2105.dt031g.bathingsites

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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
import java.util.*
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    //https://www.youtube.com/watch?v=FotQIcC91V4 denna fungerade ish

    //https://developers.google.com/codelabs/maps-platform/maps-platform-101-android#5 mycket från denna

    //private lateinit var currentLocation : Location
    private lateinit var lastLocation : Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var radius by Delegates.notNull<Double>()


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
    }

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
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }


    private fun setUpMap() {

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
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
                makeRadiusAndGetSites(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 8f))
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun makeRadiusAndGetSites(currentLatLong: LatLng) {


        // Get the url from settings.
        val preferences = getSharedPreferences("maps", Context.MODE_PRIVATE)
        val radiuskm = preferences.getString("mapsValue", "")?.toDouble()
        val radiusMeters = radiuskm?.times(1000)
        if (radiusMeters != null) {
            radius = radiusMeters
        }


        //https://developers.google.com/codelabs/maps-platform/maps-platform-101-android#8
        var circle: Circle? = null
        circle?.remove()
        circle = radiusMeters?.let {
            CircleOptions()
                .center(currentLatLong)
                .radius(it)  //här ska ju settingsvärdet in
                .fillColor(ContextCompat.getColor(this, R.color.transparent_purple))
                .strokeColor(ContextCompat.getColor(this, R.color.purple_700))
        }?.let {
            mMap.addCircle(
                it
            )
        }

        // Get the bathing sites from the database.
        lateinit var bathingSites: List<BathingSite>
        // Reading can take time, do it in a coroutine.
        GlobalScope.launch {
            bathingSites = appDatabase.bathingSiteDao().getAllSites()
            displaySites(bathingSites, currentLatLong)
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun displaySites(bathingSites: List<BathingSite>, currentLatLong: LatLng) {

        // Dispatch on main.
        GlobalScope.launch(Dispatchers.Main) {
            bathingSites.forEach {

                // Get coordinates for bathing sites that only have an address. https://stackoverflow.com/questions/9698328/how-to-get-coordinates-of-an-address-in-android
                if (it.latitude == null || it.longitude == null) {

                    val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                    val addresses: List<Address>? =
                        it.address?.let { it1 -> geocoder.getFromLocationName(it1, 1) }

                    if (addresses != null) {
                        if (addresses.isNotEmpty()) {
                            val latitude = addresses[0].latitude
                            val longitude = addresses[0].longitude

                            it.latitude = latitude.toFloat()
                            it.longitude = longitude.toFloat()
                        }
                    }
                }

                // Make a latLong of the coordinates from the bathing site.
                val latLngSite = it.latitude?.let { it1 -> it.longitude?.let { it2 -> LatLng(it1.toDouble(), it2.toDouble()) } }

                // Calculate the distance between the site and the current location.
                // https://developers.google.com/maps/documentation/android-sdk/utility
                val distance = FloatArray(1)
                latLngSite?.latitude?.let { it1 ->
                    latLngSite.longitude.let { it2 ->
                        Location.distanceBetween(currentLatLong.latitude, currentLatLong.longitude,
                            it1, it2, distance
                        )
                    }
                }

                // Only display bathing sites within the set radius.
                if (distance[0] < radius) {

                    // Round the distance and convert to kilometers.
                    val roundedDistance = distance[0].roundToInt() / 1000

                    // Create the text with info about the bathing site.
                    val snippetText = getString(R.string.name) + it.name + getString(
                        R.string.description
                    ) + (it.description ?: "") +
                            getString(R.string.address) + (it.address ?: "") + getString(
                        R.string.longitude
                    ) + (it.longitude ?: "") +
                            getString(R.string.latitude) + (it.latitude ?: "") + getString(
                        R.string.grade
                    ) + (it.grade ?: "") + getString(R.string.water_temp) +
                            (it.waterTemp ?: "") + getString(R.string.date_water) + (it.dateTemp ?: "") +
                            getString(R.string.distance) + roundedDistance + getString(R.string.kilometer)

                    // In order to fit all text in the snippet, a custom info window is needed.
                    mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this@MapsActivity))

                    // Make the marker.
                    latLngSite?.let { it1 ->
                        MarkerOptions()
                            .title(it.name)
                            .snippet(snippetText)
                            .position(it1)
                    }?.let { it2 ->
                        mMap.addMarker(
                            it2
                        )
                    }
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
