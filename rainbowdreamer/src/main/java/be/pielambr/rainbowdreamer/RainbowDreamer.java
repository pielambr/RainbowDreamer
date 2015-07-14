package be.pielambr.rainbowdreamer;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
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

    private String[] colors;
    private String selectedColor;
    private String message;
    private String title;
    private OnColorSelectedListener listener;
    private boolean initialised;
    private int checkmarkColor;

    public RainbowDreamer() {
        this.colors = new String[0];
        this.checkmarkColor = -1;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(this.getActivity())
                .setTitle(getTitle())
                .setMessage(getMessage())
                .setView(buildView()).create();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            this.colors = savedInstanceState.getStringArray(KEY_COLORS);
            this.selectedColor = savedInstanceState.getString(KEY_SELECTED);
            this.checkmarkColor = savedInstanceState.getInt(KEY_CHECKMARK);
        }
    }

    /**
     * Builds the view for the color picker
     * @return Returns a view for the color picker, with a table layout as root
     */
    private View buildView() {
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
        return layout;
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
     * Toggles whether this view is already initialised or not
     */
    private void toggleInitialised() {
        this.initialised = !initialised;
    }

    /**
     * Returns the view for a single color
     * @param color The color string that was passed by the user
     * @return A view for the passed color
     */
    private View getColoredCircle(final String color) {
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
        if(color.equals(selectedColor)) {
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
    private Drawable getColoredBackground(String color) {
        int bgColor = Color.parseColor(color);
        Drawable circle = getResources().getDrawable(R.drawable.circle);
        circle.setColorFilter(
                new PorterDuffColorFilter(bgColor, PorterDuff.Mode.MULTIPLY));
        return circle;
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
    public void setColors(String[] colors) {
        this.colors = colors;
    }

    /**
     * Select a color in the color picker
     * @param color The color that needs to be selected
     */
    public void setSelectedColor(String color) {
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
        saveState.putStringArray(KEY_COLORS, colors);
        saveState.putString(KEY_SELECTED, selectedColor);
        saveState.putInt(KEY_CHECKMARK, checkmarkColor);
    }

    /**
     * Returns the bottom margin of the dialog fragment
     * @return The bottom margin of the dialog fragment in pixels
     */
    private int getBottomMargin() {
        return getResources().getDimensionPixelSize(R.dimen.dialog_bottom_margin);
    }

    private int getCheckmarkSize() {
        return getResources().getDimensionPixelSize(R.dimen.checkmark_size);
    }

    public void setCheckmarkColor(String color) {
        this.checkmarkColor = Color.parseColor(color);
    }

    public int getCheckmarkColor() {
        return this.checkmarkColor;
    }
}
