package com.jmurphy.expensetracker

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.jmurphy.expensetracker.ui.ExpenseListFragment
import com.jmurphy.expensetracker.ui.FilterDrawerFragment


class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<ExpenseViewModel>()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var listFragment: ExpenseListFragment
    private lateinit var filterDrawer: FilterDrawerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

        if (savedInstanceState==null){

            var ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            listFragment = ExpenseListFragment.newInstance()
            ft.replace(R.id.fragment_holder, listFragment)
            ft.commit()

            ft = supportFragmentManager.beginTransaction()
            filterDrawer = FilterDrawerFragment.newInstance()
            ft.replace(R.id.right_drawer, filterDrawer)
            ft.commit()
        }
        else{
            listFragment = supportFragmentManager.findFragmentById(R.id.fragment_holder) as ExpenseListFragment
            filterDrawer = supportFragmentManager.findFragmentById(R.id.right_drawer) as FilterDrawerFragment
        }

        viewModel.getFilters().observe(this){
            drawerLayout.closeDrawer(GravityCompat.END)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END)
        }
        else{
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.filters){
            if (!drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.openDrawer(GravityCompat.END)
            else
                drawerLayout.closeDrawer(GravityCompat.END)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}