package com.zhou.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter crimeAdapter;
    private boolean mSubtitleVisible;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private TextView mTextView;
    private Button mClick;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        mTextView = (TextView)view.findViewById(R.id.crime_set_empty_text_view);
        mClick = (Button)view.findViewById(R.id.click);
        mClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime = new Crime();
                CrimeLab.getCrimeLab(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getmId());
                startActivity(intent);
            }
        });



        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.getCrimeLab(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getmId());
                startActivity(intent);
                return true;

            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
//        String subtitle = getString(R.string.subtitle_format, crimeCount);
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount,crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);

    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (crimeAdapter == null) {
            crimeAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(crimeAdapter);
        }else {
            crimeAdapter.setCrimes(crimes);
            crimeAdapter.notifyDataSetChanged();
        }

        if(crimes.size() > 0){
            mTextView.setVisibility(View.GONE);
            mClick.setVisibility(View.GONE);
        }else{
            mTextView.setVisibility(View.VISIBLE);
            mClick.setVisibility(View.VISIBLE);
        }

        updateSubtitle();

    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater.inflate(R.layout.list_item_crime, viewGroup, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getmTitle());
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String mDate = simpleDateFormat.format(mCrime.getmDate());
            String mDate = (String) DateFormat.format("E,MM/dd,yyyy",mCrime.getmDate());

            mDateTextView.setText(mDate);
            mSolvedImageView.setVisibility(crime.ismSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
//            Toast.makeText(getActivity(), mCrime.getmTitle() + " clicked!",
//                    Toast.LENGTH_SHORT).show();

//            Intent intent = new Intent(getActivity(), CrimeActivity.class);
//            Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getmId());
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getmId());
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new CrimeHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int i) {
            Crime crime = mCrimes.get(i);
            crimeHolder.bind(crime);

        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }
    }


}
