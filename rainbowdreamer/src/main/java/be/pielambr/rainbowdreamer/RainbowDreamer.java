package be.pielambr.rainbowdreamer;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by Pieterjan Lambrecht on 12/07/2015.
 */
public class RainbowDreamer extends DialogFragment {

    private static final String KEY_COLORS = "_colors";

    private String[] colors;
    private String selectedColor;
    private String message;
    private String title;
    private OnColorSelectedListener listener;
    private boolean initialised;

    public RainbowDreamer() {
        this.colors = new String[0];
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new AlertDialog.Builder(this.getActivity())
                .setTitle(getTitle())
                .setMessage(getMessage())
                .setView(buildView()).create();
        return dialog;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            this.colors = savedInstanceState.getStringArray(KEY_COLORS);
        }
    }

    private View buildView() {
        TableLayout layout = new TableLayout(getActivity());
        if(this.colors.length < 1) {
            return layout;
        }
        layout.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(v.getWidth() != 0 && !initialised) {
                    toggleInitialised();
                    addColorRows((TableLayout) v);
                }

            }
        });
        return layout;
    }

    private void addColorRows(TableLayout layout) {
        layout.removeAllViews();
        View[] colors = getColors();
        int width = layout.getWidth();
        int colorWidth = getWidth() + 2 * getMargin();
        int nbColumns = (int) Math.floor(width / (double) colorWidth);
        int nbRows = (int) Math.ceil(colors.length / (double) nbColumns);
        TableRow row = null;
        for(int i = 0; i < nbRows; i++) {
            for(int j = 0; j < nbColumns; j++) {
                if(j == 0) {
                    row = createTableRow();
                }
                if(i * nbColumns + j < colors.length) {
                    row.addView(colors[i * nbColumns + j]);
                }
                if(j == nbColumns - 1) {
                    layout.addView(row);
                }
            }
        }
        layout.setMinimumHeight(colorWidth * nbRows);
    }

    private TableRow createTableRow() {
        TableRow row = new TableRow(getActivity());
        row.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return row;
    }

    private View[] getColors() {
        View[] views = new View[colors.length];
        for(int i = 0; i < colors.length; i++) {
            views[i] = getColoredCircle(colors[i]);
        }
        return views;
    }

    private void toggleInitialised() {
        this.initialised = !initialised;
    }

    private View getColoredCircle(final String color) {
        int m = getMargin();
        TextView view = new TextView(this.getActivity());
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                getWidth(), getWidth());
        params.setMargins(m, m, m, m);
        view.setLayoutParams(params);
        view.setBackgroundDrawable(getColoredBackground(color));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.selectColor(Color.parseColor(color));
                }
                dismiss();
            }
        });
        return view;
    }

    private Drawable getColoredBackground(String color) {
        int bgColor = Color.parseColor(color);
        Drawable circle = getResources().getDrawable(R.drawable.circle);
        circle.setColorFilter(
                new PorterDuffColorFilter(bgColor, PorterDuff.Mode.MULTIPLY));
        return circle;
    }

    private String getTitle() {
        return title == null ? getString(R.string.app_name) : title;
    }

    private String getMessage() {
        return message == null ? getString(R.string.pick_color) : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setColors(String[] colors) {
        this.colors = colors;
    }

    public void setSelectedColor(String color) {
        this.selectedColor = color;
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }

    private int getWidth() {
        return getResources().getDimensionPixelSize(R.dimen.color_width);
    }

    private int getMargin() {
        return getResources().getDimensionPixelSize(R.dimen.color_margin);
    }

    @Override
    public void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
        saveState.putStringArray(KEY_COLORS, colors);
    }
}
