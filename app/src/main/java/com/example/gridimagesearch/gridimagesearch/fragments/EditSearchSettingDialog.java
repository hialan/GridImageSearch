package com.example.gridimagesearch.gridimagesearch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.gridimagesearch.gridimagesearch.R;
import com.example.gridimagesearch.gridimagesearch.models.SearchSetting;

public class EditSearchSettingDialog extends DialogFragment {

    public interface EditSearchSettingDialogListener {
        void onFinishEditSeachSettingDialog(SearchSetting setting);
    }

    public EditSearchSettingDialog() { }

    public static EditSearchSettingDialog newInstance(SearchSetting setting){
        EditSearchSettingDialog frag = new EditSearchSettingDialog();
        Bundle args = new Bundle();
        args.putSerializable("setting", setting);
        frag.setArguments(args);
        return frag;
    }

    private SearchSetting setting;
    private Spinner spImageSize;
    private Spinner spImageColor;
    private Spinner spImageType;
    private Button btSaveSetting;
    private EditText etSite;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_filter_settings, container);
        setting = (SearchSetting) getArguments().getSerializable("setting");

        spImageSize = (Spinner) view.findViewById(R.id.spImageSize);
        spImageColor = (Spinner) view.findViewById(R.id.spImageColor);
        spImageType = (Spinner) view.findViewById(R.id.spImageType);
        setSpinnerAdapter(spImageSize, R.array.image_size_array);
        setSpinnerAdapter(spImageColor, R.array.image_color_array);
        setSpinnerAdapter(spImageType, R.array.image_type);

        spImageSize.setSelection(setting.size);
        spImageType.setSelection(setting.type);
        spImageColor.setSelection(setting.color);

        etSite = (EditText) view.findViewById(R.id.etSite);

        btSaveSetting = (Button) view.findViewById(R.id.btSaveSetting);
        btSaveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting.size = spImageSize.getSelectedItemPosition();
                setting.type = spImageType.getSelectedItemPosition();
                setting.color = spImageColor.getSelectedItemPosition();
                setting.site = etSite.getText().toString();

                EditSearchSettingDialogListener listener = (EditSearchSettingDialogListener) getActivity();
                listener.onFinishEditSeachSettingDialog(setting);
                dismiss();
            }
        });

        return view;
    }

    private void setSpinnerAdapter(Spinner spinner, int array_resource_id) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), array_resource_id, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
