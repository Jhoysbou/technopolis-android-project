package com.example.technopolis.screens.scheduleritems;

import android.view.View;
import android.widget.Toast;

import com.example.technopolis.BaseActivity;
import com.example.technopolis.R;
import com.example.technopolis.api.ApiHelper;
import com.example.technopolis.scheduler.model.SchedulerItem;
import com.example.technopolis.scheduler.service.SchedulerItemService;
import com.example.technopolis.screens.common.mvp.MvpPresenter;
import com.example.technopolis.screens.common.nav.BackPressDispatcher;
import com.example.technopolis.screens.common.nav.BackPressedListener;
import com.example.technopolis.screens.common.nav.ScreenNavigator;
import com.example.technopolis.util.ThreadPoster;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.everything.android.ui.overscroll.IOverScrollStateListener;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;

import static me.everything.android.ui.overscroll.IOverScrollState.STATE_BOUNCE_BACK;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_DRAG_START_SIDE;

public class SchedulerItemsPresenter implements MvpPresenter<SchedulerItemsMvpView>,
        BackPressedListener {
    private static final String RESPONSE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final ScreenNavigator screenNavigator;
    private final BackPressDispatcher backPressDispatcher;
    private final SchedulerItemService schedulerItemService;
    private final ThreadPoster mainThreadPoster;
    private final ApiHelper apiHelper;
    private final BaseActivity activity;

    private SchedulerItemsMvpView view;
    private Thread thread;
    private float currentOverScrollOffset;

    public SchedulerItemsPresenter(ScreenNavigator screenNavigator, BaseActivity activity,
                                   SchedulerItemService schedulerItemService, ThreadPoster mainThreadPoster, ApiHelper apiHelper) {
        this.screenNavigator = screenNavigator;
        this.backPressDispatcher = activity;
        this.activity = activity;
        this.schedulerItemService = schedulerItemService;
        this.mainThreadPoster = mainThreadPoster;
        this.apiHelper = apiHelper;
    }

    @Override
    public void bindView(SchedulerItemsMvpView view) {
        this.view = view;
        setOnReloadListener();
        loadItems();
    }

    private void setOnReloadListener() {
        IOverScrollStateListener overScrollStateListener = (decor, oldState, newState) -> {
            if (newState == STATE_BOUNCE_BACK) {
                if (oldState == STATE_DRAG_START_SIDE && currentOverScrollOffset > 100) {
                    thread = new Thread(() -> {
                        final List<SchedulerItem> schedulerItems = schedulerItemService.requestFromApi();
                        if (!apiHelper.showMessageIfExist(schedulerItemService.getApi(), screenNavigator, this::loadItems)) {
                            final List<View.OnClickListener> listeners = createListeners(schedulerItems);
                            final List<IsOnlineSupplier> suppliers = createEstimateSupplier(schedulerItems.size());
                            if (thread != null && !thread.isInterrupted()) {
                                mainThreadPoster.post(() -> onItemsLoaded(schedulerItems, listeners, suppliers, 0));
                            }
                        }
                    });
                    thread.start();
                }
            }
        };
        IOverScrollUpdateListener overScrollUpdateListener = (decor, state, offset) -> {
            currentOverScrollOffset = offset;
        };
        view.setOnReloadListener(overScrollStateListener, overScrollUpdateListener);
    }

    private void loadItems() {
        thread = new Thread(() -> {
            final List<SchedulerItem> schedulerItems = schedulerItemService.items();
            if (!apiHelper.showMessageIfExist(schedulerItemService.getApi(), screenNavigator, this::loadItems)) {
                final int actualDayPosition = calculateActualDayPosition(schedulerItems);
                final List<IsOnlineSupplier> suppliers = createEstimateSupplier(schedulerItems.size());
                final List<View.OnClickListener> listeners = createListeners(schedulerItems);
                if (thread != null && !thread.isInterrupted()) {
                    mainThreadPoster.post(() -> onItemsLoaded(schedulerItems, listeners, suppliers, actualDayPosition));
                }
            }
        });
        thread.start();
    }

    private int calculateActualDayPosition(List<SchedulerItem> schedulerItems) {
        int actualPosition = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RESPONSE_FORMAT, new Locale("ru"));
        String date = simpleDateFormat.format(new Date());
        for (SchedulerItem schedulerItem : schedulerItems) {
            if (date.compareTo(schedulerItem.getDate()) > 0) {
                actualPosition++;
            }
        }
        actualPosition--;
        actualPosition = Math.max(actualPosition, 0);
        return actualPosition;
    }

    private void onItemsLoaded(List<SchedulerItem> schedulerItems, List<View.OnClickListener> listeners, List<IsOnlineSupplier> suppliers, int actualPosition) {
        view.bindData(schedulerItems, listeners, suppliers, actualPosition);
    }

    @Override
    public void onStart() {
        view.registerListener(this);
        backPressDispatcher.registerListener(this);
    }

    @Override
    public void onStop() {
        view.unregisterListener(this);
        backPressDispatcher.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        thread.interrupt();
        thread = null;
        view = null;
    }

    public void onCheckInClicked(long id) {
        thread = new Thread(() -> {
            final List<SchedulerItem> schedulerItems = schedulerItemService.checkInItem(id);
            if (!apiHelper.showMessageIfExist(schedulerItemService.getApi(), screenNavigator, this::loadItems)) {
                final int actualDayPosition = calculateActualDayPosition(schedulerItems);
                final List<View.OnClickListener> listeners = createListeners(schedulerItems);
                final List<IsOnlineSupplier> suppliers = createEstimateSupplier(schedulerItems.size());
                mainThreadPoster.post(() -> onItemsLoaded(schedulerItems, listeners, suppliers, actualDayPosition));
            }
        });
        thread.start();
    }

    private List<View.OnClickListener> createListeners(List<SchedulerItem> items) {
        List<View.OnClickListener> listeners = new ArrayList<>();
        for (SchedulerItem schedulerItem : items) {
            listeners.add(v -> {
                if (apiHelper.isOnline()) {
                    onCheckInClicked(schedulerItem.getId());
                } else {
                    activity.runOnUiThread(() -> Toast.makeText(activity, R.string.networkError, Toast.LENGTH_SHORT).show());
                }
            });
        }
        return listeners;
    }

    private List<IsOnlineSupplier> createEstimateSupplier(final int count) {
        List<IsOnlineSupplier> suppliers = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            suppliers.add(() -> {
                if (!apiHelper.isOnline()) {
                    activity.runOnUiThread(() -> Toast.makeText(activity, R.string.networkError, Toast.LENGTH_SHORT).show());
                    return false;
                }
                return true;
            });
        }
        return suppliers;
    }

    @Override
    public boolean onBackPressed() {
        return screenNavigator.navigateUp();
    }
}
