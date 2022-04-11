package android.example.myapplication.ui.bottomNavigation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MsgViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MsgViewModel(){
        mText = new MutableLiveData<>();
        mText.setValue("Message");
    }

    public LiveData<String> getText() {return mText;}
}
