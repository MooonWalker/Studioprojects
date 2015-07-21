package ati.lunarmessages;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

public class MyTabListener<T extends Fragment> implements ActionBar.TabListener
{
    private Fragment fragment;
    public MyTabListener(Fragment fragment)
    {
        this.fragment=fragment;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        ft.replace(R.id.container, fragment, null);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        if (fragment != null)
        {

            ft.remove(fragment);
        }

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
    {

    }
}
