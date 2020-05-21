package com.example.technopolis;

import android.app.Application;

import androidx.annotation.NonNull;

import com.android.volley.toolbox.Volley;
import com.example.technopolis.api.ApiHelper;
import com.example.technopolis.api.MailApi;
import com.example.technopolis.api.MailApiImpl;
import com.example.technopolis.group.repo.GroupItemRepo;
import com.example.technopolis.group.repo.GroupItemRepoImpl;
import com.example.technopolis.group.service.FindGroupItemService;
import com.example.technopolis.news.repo.NewsItemRepository;
import com.example.technopolis.news.repo.NewsItemRepositoryImpl;
import com.example.technopolis.news.repo.NewsItemRepositoryImplSubs;
import com.example.technopolis.news.service.NewsItemService;
import com.example.technopolis.profile.repo.UserProfileRepo;
import com.example.technopolis.profile.repo.UserProfileRepoImpl;
import com.example.technopolis.profile.service.ProfileService;
import com.example.technopolis.scheduler.repo.SchedulerItemRepo;
import com.example.technopolis.scheduler.repo.SchedulerItemRepoImpl;
import com.example.technopolis.scheduler.service.SchedulerItemService;
import com.example.technopolis.user.model.User;
import com.example.technopolis.user.service.AuthService;
import com.example.technopolis.util.MainThreadPoster;
import com.example.technopolis.util.ThreadPoster;


public class App extends Application {

    private boolean authorized = false;
    private MailApi api;
    private ApiHelper apiHelper;
    private MainThreadPoster mainThreadPoster;

    private User user;
    private AuthService authService;
    private SchedulerItemService schedulerItemService;
    private SchedulerItemRepo schedulerItemRepo;

    private NewsItemRepository newsItemRepo;
    private NewsItemRepository subsItemRepo;
    private NewsItemService newsItemService;

    private ProfileService profileService;
    private UserProfileRepo userProfileRepo;
    private FindGroupItemService findGroupItemService;
    private GroupItemRepo groupItemRepo;

    public void setUser(@NonNull User user) {
        this.user = user;
    }

    public void setSchedulerItemRepo(@NonNull SchedulerItemRepo repo) {
        this.schedulerItemRepo = repo;
    }

    public void setNewsItemRepo(@NonNull NewsItemRepository newsItemRepo) {
        this.newsItemRepo = newsItemRepo;
    }

    public void setSubsItemRepo(@NonNull NewsItemRepository subsItemRepo) {
        this.subsItemRepo = subsItemRepo;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public MailApi provideMailApi() {
        if (api == null) {
            api = new MailApiImpl(Volley.newRequestQueue(this), provideUser(), provideApiHelper());
        }
        return api;
    }

    public ApiHelper provideApiHelper() {
        if (apiHelper == null) {
            apiHelper = new ApiHelper();
        }
        return apiHelper;
    }

    public AuthService provideAuthService() {
        if (authService == null) {
            authService = new AuthService(provideMailApi(), provideUser());
        }
        return authService;
    }

    public ThreadPoster provideMainThreadPoster() {
        if (mainThreadPoster == null) {
            mainThreadPoster = new MainThreadPoster();
        }
        return mainThreadPoster;
    }

    public User provideUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public UserProfileRepo provideUserProfileRepo() {
        if (userProfileRepo == null) {
            userProfileRepo = new UserProfileRepoImpl();
        }
        return userProfileRepo;
    }

    public ProfileService provideProfileService() {
        if (profileService == null) {
            profileService = new ProfileService(provideUserProfileRepo(), provideMailApi(), getApplicationContext());
        }
        return profileService;
    }

    public SchedulerItemRepo provideSchedulerItemRepo() {
        if (schedulerItemRepo == null) {
            schedulerItemRepo = new SchedulerItemRepoImpl();
        }
        return schedulerItemRepo;
    }

    public SchedulerItemService provideSchedulerItemService() {
        if (schedulerItemService == null) {
            schedulerItemService = new SchedulerItemService(provideSchedulerItemRepo(), provideMailApi());
        }
        return schedulerItemService;
    }


    public NewsItemRepository provideNewsItemRepo() {
        if (newsItemRepo == null) {
            newsItemRepo = new NewsItemRepositoryImpl();
        }
        return newsItemRepo;
    }

    public NewsItemRepository provideSubsItemRepo() {
        if (subsItemRepo == null) {
            subsItemRepo = new NewsItemRepositoryImplSubs();
        }
        return subsItemRepo;
    }

    public NewsItemService provideNewsItemService() {
        if (newsItemService == null) {
            newsItemService = new NewsItemService(provideNewsItemRepo(), provideSubsItemRepo(), provideMailApi());
        }
        return newsItemService;
    }

    public GroupItemRepo provideGroupItemRepo() {
        if (groupItemRepo == null) {
            groupItemRepo = new GroupItemRepoImpl();
        }
        return groupItemRepo;
    }

    public FindGroupItemService provideFindGroupItemService() {
        if (findGroupItemService == null) {
            findGroupItemService = new FindGroupItemService(provideGroupItemRepo(), provideMailApi());
        }
        return findGroupItemService;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}