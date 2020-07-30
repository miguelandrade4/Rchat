package com.example.james.rchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionsPagerAdapter extends FragmentPagerAdapter{


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                TopicsFragment topicsFragment = new TopicsFragment();
                return topicsFragment;

            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;

            case 2:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 3:
                BombsFragment bombsFragment = new BombsFragment();
                return bombsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position){

        switch(position) {
            case 0:
                return "TOPICS";

            case 1:
                return "GROUPS";

            case 2:
                return "CHATS";
            case 3:
                return "BOMBS";

            default:
                return null;
        }

    }

}
