package in.co.eko.fundu.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import in.co.eko.fundu.fragments.MapFragment;

/**
 * Created by divyanshu.jain on 7/1/2016.
 */
public class TabViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public TabViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    /**
     * To replace the fragment in one of the tabs
     * @param fragment
     * @param title
     * @param position
     * @return
     */

    public Fragment replaceFragment(Fragment fragment, String title, int position){
        Fragment removed = mFragmentList.remove(position);
        mFragmentTitleList.remove(position);
        mFragmentList.add(position,fragment);
        mFragmentTitleList.add(position,title);
        return removed;
    }

    public Fragment getFragment(int pos) {
        return mFragmentList.get(pos);
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof MapFragment) {
            return POSITION_UNCHANGED; // don't force a reload
        } else {
            // POSITION_NONE means something like: this fragment is no longer valid
            // triggering the ViewPager to re-build the instance of this fragment.
            return POSITION_NONE;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
