package mx.ipn.escom.tienditappclient.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TableRow
import androidx.fragment.app.Fragment
import mx.ipn.escom.tienditappclient.R

class dashboard : AppCompatActivity() {
    var isContentOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        adjustMenuToScreen()

        findViewById<ImageButton>(R.id.dashboard_bar_menuButton).setOnClickListener{
            slideMenu()
        }

        findViewById<TableRow>(R.id.dashboard_menu_viewCart).setOnClickListener {
            loadFragment(view_cart())
        }
        findViewById<TableRow>(R.id.dashboard_menu_viewProducts).setOnClickListener {
            loadFragment(view_products())
        }
        findViewById<TableRow>(R.id.dashboard_menu_editProducts).setOnClickListener {
            loadFragment(edit_products())
        }
    }

    private fun slideMenu(){
        val layout = findViewById<RelativeLayout>(R.id.dashboard_content_background)
        val layoutParams = layout.layoutParams as RelativeLayout.LayoutParams
        val shadow = findViewById<RelativeLayout>(R.id.pantalla_dashboard_degradado)
        val shadowParams = shadow.layoutParams as RelativeLayout.LayoutParams
        val a: Animation

        if(isContentOn){
            a = object: Animation(){
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    super.applyTransformation(interpolatedTime, t)
                    var newLeftMargin = resources.displayMetrics.density
                    var newRightMargin = resources.displayMetrics.density
                    var shadowLeftMargin = newLeftMargin
                    /*Close Menu*/
                    newLeftMargin *= 0
                    newRightMargin *= 0
                    shadowLeftMargin *= 0
                    layoutParams.setMargins((layoutParams.leftMargin + (newLeftMargin-layoutParams.leftMargin)*interpolatedTime).toInt(),0,(layoutParams.rightMargin + (newRightMargin-layoutParams.rightMargin)*interpolatedTime).toInt(),0)
                    shadowParams.setMargins((shadowParams.leftMargin + (shadowLeftMargin-shadowParams.leftMargin)*interpolatedTime).toInt(),0,0,0)
                    layout.layoutParams = layoutParams
                    shadow.layoutParams = shadowParams
                }
            }
            a.setDuration(500) // in ms
        }else{
            a = object: Animation(){
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    super.applyTransformation(interpolatedTime, t)
                    val widthDp = (resources.displayMetrics.widthPixels * 0.8).toInt()
                    var newLeftMargin = 1
                    var newRightMargin = 1
                    var shadowLeftMargin = newLeftMargin
                    /*Open Menu*/
                    newLeftMargin = widthDp
                    newRightMargin *= -widthDp
                    shadowLeftMargin *= widthDp-(4*resources.displayMetrics.density).toInt()
                    layoutParams.setMargins((layoutParams.leftMargin + (newLeftMargin-layoutParams.leftMargin)*interpolatedTime).toInt(),0,(layoutParams.rightMargin + (newRightMargin-layoutParams.rightMargin)*interpolatedTime).toInt(),0)
                    shadowParams.setMargins((shadowParams.leftMargin + (shadowLeftMargin-shadowParams.leftMargin)*interpolatedTime).toInt(),0,0,0)
                    layout.layoutParams = layoutParams
                    shadow.layoutParams = shadowParams
                }
            }
            a.setDuration(1000) // in ms
        }

        isContentOn = !isContentOn
        layout.startAnimation(a);
    }

    private fun adjustMenuToScreen(){
        val menuLayout = findViewById<RelativeLayout>(R.id.dashboard_menu_background)
        val params = menuLayout.layoutParams
        params.width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        menuLayout.layoutParams = params
    }

    override fun onBackPressed() {
        if(isContentOn)
            slideMenu()
        else
            finish()
    }

    private fun loadFragment(fragment : Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashboard_content_fragmentContent, fragment)
        fragmentTransaction.commit()
        slideMenu()
    }
}