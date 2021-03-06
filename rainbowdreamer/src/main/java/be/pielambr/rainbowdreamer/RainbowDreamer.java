package be.pielambr.rainbowdreamer;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by Pieterjan Lambrecht on 12/07/2015.
 */
public class RainbowDreamer extends DialogFragment {

    private static final String KEY_COLORS = "_colors";
    private static final String KEY_SELECTED = "_selected";
    private static final String KEY_CHECKMARK = "_checkmark";

    private int[] colors;
    private int selectedColor;
    private String message;
    private String title;
    private OnColorSelectedListener listener;
    private int checkmarkColor;

    private boolean initialised;

    public RainbowDreamer() {
        this.colors = new int[0];
        this.checkmarkColor = -1;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = buildView();
        return new AlertDialog.Builder(getActivity())
                .setTitle(getTitle())
                .setMessage(getMessage())
                .setView(view)
                .create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            this.colors = savedInstanceState.getIntArray(KEY_COLORS);
            this.selectedColor = savedInstanceState.getInt(KEY_SELECTED);
            this.checkmarkColor = savedInstanceState.getInt(KEY_CHECKMARK);
        }
    }

    /**
     * Builds the view for the color picker
     * @return Returns a view for the color picker, with a table layout as root
     */
    private View buildView() {
        ScrollView scrollView = new ScrollView(getActivity());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        TableLayout layout = createTableLayout();
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
        scrollView.addView(layout);
        return scrollView;
    }

    /**
     * Adds table rows to the table layout, containing the colors
     * @param layout The table layout to which the rows will be added
     */
    private void addColorRows(TableLayout layout) {
        layout.removeAllViews();
        View[] colors = getColors();
        int width = layout.getWidth() - 2 * getMargin();
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
    }

    /**
     * Creates a new table row that wraps it's content
     * @return Returns a table row for the table layout
     */
    private TableRow createTableRow() {
        TableRow row = new TableRow(getActivity());
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        return row;
    }

    /**
     * Creates a new table layout
     * @return The newly created table layout
     */
    private TableLayout createTableLayout() {
        TableLayout layout = new TableLayout(getActivity());
        if(this.colors.length < 1) {
            return layout;
        }
        layout.setStretchAllColumns(true);
        layout.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        layout.setPadding(getMargin(),
                getMargin(), getMargin(),
                getBottomMargin());
        return layout;
    }

    /**
     * Gets the views for all the different color
     * @return An array with the views for all the colors
     */
    private View[] getColors() {
        View[] views = new View[colors.length];
        for(int i = 0; i < colors.length; i++) {
            views[i] = getColoredCircle(colors[i]);
        }
        return views;
    }

    /**
     * Returns the view for a single color
     * @param color The color resource that was passed by the user
     * @return A view for the passed color
     */
    private View getColoredCircle(final int color) {
        RelativeLayout layout = new RelativeLayout(this.getActivity());
        layout.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        TextView view = new TextView(this.getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                getWidth(), getWidth());
        params.setMargins(getMargin(), getMargin(),
                getMargin(), getMargin());
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        view.setLayoutParams(params);
        // We use this for compatibility with lower Android versions
        view.setBackgroundDrawable(getColoredBackground(getResources().getColor(color)));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.selectColor(getResources().getColor(color));
                }
                toggleInitialised();
                dismiss();
            }
        });
        if(color == selectedColor) {
            view.setText(Html.fromHtml("&#x2713;"));
            view.setTextSize(getCheckmarkSize());
            view.setGravity(Gravity.CENTER);
            view.setTextColor(getCheckmarkColor());
        }
        layout.addView(view);
        return layout;
    }

    /**
     * Returns a drawable with the given color
     * @param color Background color for the drawable
     * @return A drawable colored with the given string
     */
    private Drawable getColoredBackground(int color) {
        Drawable circle = getResources().getDrawable(R.drawable.circle);
        circle.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        return circle;
    }

    /**
     * Toggles wether the dialog layout has been initialised
     */
    private void toggleInitialised() {
        initialised = !initialised;
    }

    /**
     * Returns the title for the color picker
     * @return The title for the color picker
     */
    public String getTitle() {
        return title == null ? getString(R.string.app_name) : title;
    }

    /**
     * Returns the message that is underneath the title for the color picker
     * @return The message underneath the title of the color picker
     */
    private String getMessage() {
        return message == null ? getString(R.string.pick_color) : message;
    }

    /**
     * Sets a custom message on the color picker
     * @param message The message for the color picker to display
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets a custom title on the color picker
     * @param title The title for the color picker
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets an array of colors that the color picker should show
     * See documentation for Color.parse(...) for what you can pass
     * @param colors An error of color strings
     */
    public void setColors(int[] colors) {
        this.colors = colors;
    }

    /**
     * Select a color in the color picker
     * @param color The color that needs to be selected
     */
    public void setSelectedColor(int color) {
        this.selectedColor = color;
    }

    /**
     * Set a listener on the color picker that should listen for color selection
     * @param listener A listener that listens for color selection events
     */
    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * Returns the width of a color view
     * @return The width of a color view, in pixels
     */
    private int getWidth() {
        return getResources().getDimensionPixelSize(R.dimen.color_width);
    }

    /**
     * Gets the margin that is between the different color views
     * @return The margin between the color views in pixels
     */
    private int getMargin() {
        return getResources().getDimensionPixelSize(R.dimen.color_margin);
    }

    @Override
    public void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
        saveState.putIntArray(KEY_COLORS, colors);
        saveState.putInt(KEY_SELECTED, selectedColor);
        saveState.putInt(KEY_CHECKMARK, checkmarkColor);
    }

    /**
     * Returns the bottom margin of the dialog fragment
     * @return The bottom margin of the dialog fragment in pixels
     */
    private int getBottomMargin() {
        return getResources().getDimensionPixelSize(R.dimen.dialog_bottom_margin);
    }

    /**
     * Gets size of checkmark
     * @return The size of the checkmark in pixels
     */
    private int getCheckmarkSize() {
        return getResources().getDimensionPixelSize(R.dimen.checkmark_size);
    }

    /**
     * Sets the color of the checkmark
     * @param color The color string of the checkmark
     */
    public void setCheckmarkColor(String color) {
        this.checkmarkColor = Color.parseColor(color);
    }

    /**
     * Returns the checkmark color, defaults to white
     * @return The checkmark color
     */
    public int getCheckmarkColor() {
        return this.checkmarkColor;
    }
}
