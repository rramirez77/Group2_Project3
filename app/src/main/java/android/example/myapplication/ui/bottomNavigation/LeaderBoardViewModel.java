package android.example.myapplication.ui.bottomNavigation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LeaderBoardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LeaderBoardViewModel(){
        mText = new MutableLiveData<>();
        mText.setValue("LeaderBoard");

    }

    public LiveData<String> getText(){return mText;}


}
