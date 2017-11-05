package com.example.ljuni.lapitchat.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.ljuni.lapitchat.Fragments.FriendsFragment;
import com.example.ljuni.lapitchat.Fragments.ChatFragment;
import com.example.ljuni.lapitchat.Fragments.SolicitacoesFragment;

/**
 * Created by ljuni on 23/10/2017.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                SolicitacoesFragment solicitacoesFragment = new SolicitacoesFragment();
                return solicitacoesFragment;
            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position) {

        switch (position) {

            case 0:
                return "Requests";

            case 1:
                return "Chat";

            case 2:
                return "Amigos";

            default:
                return null;
        }
    }
}
