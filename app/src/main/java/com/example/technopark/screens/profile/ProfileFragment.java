package com.example.technopark.screens.profile;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.technopark.App;
import com.example.technopark.BaseActivity;
import com.example.technopark.R;
import com.example.technopark.profile.service.ProfileService;
import com.example.technopark.util.ThreadPoster;

public class ProfileFragment extends Fragment {

    private ProfilePresenter presenter;
    private static final String PROFILE_NAME = "user_name";
    private static final String BACK_BUTTON_TEXT = "back_button_text";

    public ProfileFragment() {
    }

    public static Fragment newInstance() {
        Fragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(PROFILE_NAME, "");
        args.putString(BACK_BUTTON_TEXT, "");
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(String username, String backButtonText) {
        Fragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(PROFILE_NAME, username);
        args.putString(BACK_BUTTON_TEXT, backButtonText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        Bundle arguments = getArguments();
        String userProfile = arguments.getString(PROFILE_NAME);
        String backButtonText = arguments.getString(BACK_BUTTON_TEXT);
        presenter = new ProfilePresenter(userProfile, backButtonText, getProfileService(), getBaseActivity().getScreenNavigator(), getMainThreadPoster(), getBaseActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ProfileViewMvpImpl view = new ProfileViewMvpImpl(inflater, container, this);
        ((BaseActivity)getActivity()).getRootViewController().setBarVisible(View.VISIBLE);
        presenter.bindView(view);
        return view.getRootView();


////        Back button
//        Bundle bundle = this.getArguments();
//        if (bundle == null) {
//            hideBackButton(v);
//        }
//
//
////        set up good scroll
//        ScrollView scrollView = (ScrollView) v.findViewById(R.id.profile_scroll_view);
//        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
//
////        Exit
//        v.findViewById(R.id.exitButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Dialog exitDialog = new Dialog(getActivity(), R.style.ExitDialogAnimation);
//                exitDialog.getWindow().getAttributes().windowAnimations = R.style.ExitDialogAnimation;
//                exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(50, 0, 0, 0)));
//                exitDialog.setContentView(R.layout.exit_popup_view);
//                exitDialog.setCancelable(true);
//                exitDialog.show();
//
//                exitDialog.findViewById(R.id.exitButtonApprove).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                      TODO: make log out
//                        exitDialog.dismiss();
//                        // ((BaseActivity)getActivity()).setAuthorizationView();
////                        Toast.makeText(getActivity(), "log out in progress", Toast.LENGTH_SHORT).show();
//
//
//                    }
//                });
//                exitDialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        exitDialog.dismiss();
//                    }
//                });
//            }
//        });
//
//
//        androidx.appcompat.widget.Toolbar toolbar = v.findViewById(R.id.toolbar);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed();
//            }
//        });
//
//        TextView textView = v.findViewById(R.id.back_to_group);
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed();
//            }
//        });
//
//        return v;
    }

    @Nullable
    private BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    
    private void hideBackButton(View v) {
        androidx.appcompat.widget.Toolbar toolBar = v.findViewById(R.id.toolbar);
        toolBar.setNavigationIcon(null);
        TextView textView = v.findViewById(R.id.back_to_group);
        textView.setVisibility(View.INVISIBLE);
    }

    private ProfileService getProfileService() {
        App app = (App) getActivity().getApplication();
        assert app != null;
        return app.provideProfileService();
    }

    private ThreadPoster getMainThreadPoster() {
        App app = (App) getActivity().getApplication();
        assert app != null;
        return app.provideMainThreadPoster();
    }
}