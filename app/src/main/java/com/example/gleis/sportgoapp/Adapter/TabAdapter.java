package com.example.gleis.sportgoapp.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.gleis.sportgoapp.Fragment.EventosFragment;
import com.example.gleis.sportgoapp.Fragment.MapaFragment;
import com.example.gleis.sportgoapp.Fragment.MeusEventosFragment;

/**
 * Created by gleis on 26/03/2018.
 */

public class TabAdapter extends FragmentStatePagerAdapter {

    private  String[] tituloAbas = {"PARTICIPANDO","EVENTOS","MAPA"};
    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new MeusEventosFragment();
                break;
            case 1:
                fragment = new EventosFragment();
                break;
            case 2:
                fragment = new MapaFragment();
                break;
        }
        return fragment ;
    }

    @Override
    public int getCount() {
        return tituloAbas.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return tituloAbas[position];
    }
}
