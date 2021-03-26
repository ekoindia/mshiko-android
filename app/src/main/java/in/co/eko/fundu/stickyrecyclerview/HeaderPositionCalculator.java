package in.co.eko.fundu.stickyrecyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Calculates the position and location of header views
 */
public class HeaderPositionCalculator {

  private final StickyRecyclerHeadersAdapter mAdapter;
  private final HeaderProvider mHeaderProvider;
  private final DimensionCalculator mDimensionCalculator;

  public HeaderPositionCalculator(StickyRecyclerHeadersAdapter adapter, HeaderProvider headerProvider, DimensionCalculator dimensionCalculator) {
    mAdapter = adapter;
    mHeaderProvider = headerProvider;
    mDimensionCalculator = dimensionCalculator;
  }

  /**
   * Determines if a view should have a sticky header.
   * The view has a sticky header if:
   * 1. It is the first element in the recycler view
   * 2. It has a valid ID associated to its position
   *
   * @param itemView given by the RecyclerView
   * @param position of the list item in question
   * @return True if the view should have a sticky header
   */
  public boolean hasStickyHeader(View itemView, int position) {
    int offset, margin;

      offset = itemView.getTop();
      margin = mDimensionCalculator.getMargins(itemView).top;

    return offset <= margin && mAdapter.getHeaderId(position) >= 0;
  }

  /**
   * Determines if an item in the list should have a header that is different than the item in the
   * list that immediately precedes it. Items with no headers will always return false.
   *
   * @param position of the list item in questions
   * @return true if this item has a different header than the previous item in the list
   * @see {@link StickyRecyclerHeadersAdapter#getHeaderId(int)}
   */
  public boolean hasNewHeader(int position) {
    return position == mAdapter.getSpeedDialListSize(0) || position == 0;
    /*if (indexOutOfBounds(position)) {
      return false;
    }

    long headerId = mAdapter.getHeaderId(position);

    if (headerId < 0) {
      return false;
    }

    long nextItemHeaderId = -1;
    int nextItemPosition = position +  -1;
    if (!indexOutOfBounds(nextItemPosition)){
      nextItemHeaderId = mAdapter.getHeaderId(nextItemPosition);
    }
    int firstItemPosition =  0;

    return position == firstItemPosition || headerId != nextItemHeaderId;*/
  }

  private boolean indexOutOfBounds(int position) {
    return position < 0 || position >= mAdapter.getItemCount();
  }

  public Rect getHeaderBounds(RecyclerView recyclerView, View header, View firstView, boolean firstHeader) {

    Rect bounds = getDefaultHeaderOffset(recyclerView, header, firstView);

    if (firstHeader && isStickyHeaderBeingPushedOffscreen(recyclerView, header)) {
      View viewAfterNextHeader = getFirstViewUnobscuredByHeader(recyclerView, header);
      int firstViewUnderHeaderPosition = recyclerView.getChildAdapterPosition(viewAfterNextHeader);
      View secondHeader = mHeaderProvider.getHeader(recyclerView, firstViewUnderHeaderPosition);
      translateHeaderWithNextHeader(recyclerView, bounds,
          header, viewAfterNextHeader, secondHeader);
    }

    return bounds;
  }

  private Rect getDefaultHeaderOffset(RecyclerView recyclerView, View header, View firstView) {
    int translationX, translationY;
    Rect headerMargins = mDimensionCalculator.getMargins(header);

      translationX = firstView.getLeft() + headerMargins.left;
      translationY = Math.max(
          firstView.getTop() - header.getHeight() - headerMargins.bottom,
          getListTop(recyclerView) + headerMargins.top);


    return new Rect(translationX, translationY, translationX + header.getWidth(),
        translationY + header.getHeight());
  }

  private boolean isStickyHeaderBeingPushedOffscreen(RecyclerView recyclerView, View stickyHeader) {
    View viewAfterHeader = getFirstViewUnobscuredByHeader(recyclerView, stickyHeader);
    int firstViewUnderHeaderPosition = recyclerView.getChildAdapterPosition(viewAfterHeader);
    if (firstViewUnderHeaderPosition == RecyclerView.NO_POSITION) {
        return false;
    }


    if (firstViewUnderHeaderPosition > 0 && hasNewHeader(firstViewUnderHeaderPosition)) {
      View nextHeader = mHeaderProvider.getHeader(recyclerView, firstViewUnderHeaderPosition);
      Rect nextHeaderMargins = mDimensionCalculator.getMargins(nextHeader);
      Rect headerMargins = mDimensionCalculator.getMargins(stickyHeader);


        int topOfNextHeader = viewAfterHeader.getTop() - nextHeaderMargins.bottom - nextHeader.getHeight() - nextHeaderMargins.top;
        int bottomOfThisHeader = recyclerView.getPaddingTop() + stickyHeader.getBottom() + headerMargins.top + headerMargins.bottom;
        if (topOfNextHeader < bottomOfThisHeader) {
          return true;
        }

    }

    return false;
  }

  private void translateHeaderWithNextHeader(RecyclerView recyclerView, Rect translation,
      View currentHeader, View viewAfterNextHeader, View nextHeader) {
    Rect nextHeaderMargins = mDimensionCalculator.getMargins(nextHeader);
    Rect stickyHeaderMargins = mDimensionCalculator.getMargins(currentHeader);
      int topOfStickyHeader = getListTop(recyclerView) + stickyHeaderMargins.top + stickyHeaderMargins.bottom;
      int shiftFromNextHeader = viewAfterNextHeader.getTop() - nextHeader.getHeight() - nextHeaderMargins.bottom - nextHeaderMargins.top - currentHeader.getHeight() - topOfStickyHeader;
      if (shiftFromNextHeader < topOfStickyHeader) {
        translation.top += shiftFromNextHeader;
      }

  }

  /**
   * Returns the first item currently in the RecyclerView that is not obscured by a header.
   *
   * @param parent Recyclerview containing all the list items
   * @return first item that is fully beneath a header
   */
  private View getFirstViewUnobscuredByHeader(RecyclerView parent, View firstHeader) {
    //boolean isReverseLayout = false;
    int step =  1;
    int from =  0;
    for (int i = from; i >= 0 && i <= parent.getChildCount() - 1; i += step) {
      View child = parent.getChildAt(i);
      if (!itemIsObscuredByHeader(parent, child, firstHeader)) {
        return child;
      }
    }
    return null;
  }


  private boolean itemIsObscuredByHeader(RecyclerView parent, View item, View header) {
    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
    Rect headerMargins = mDimensionCalculator.getMargins(header);

    int adapterPosition = parent.getChildAdapterPosition(item);
    if (adapterPosition == RecyclerView.NO_POSITION || mHeaderProvider.getHeader(parent, adapterPosition) != header) {
      // Resolves https://github.com/timehop/sticky-headers-recyclerview/issues/36
      // Handles an edge case where a trailing header is smaller than the current sticky header.
      return false;
    }

      int itemTop = item.getTop() - layoutParams.topMargin;
      int headerBottom = header.getBottom() + headerMargins.bottom + headerMargins.top;
      return itemTop <= headerBottom;


  }

  private int getListTop(RecyclerView view) {
    if (view.getLayoutManager().getClipToPadding()) {
      return view.getPaddingTop();
    } else {
      return 0;
    }
  }

  private int getListLeft(RecyclerView view) {
    if (view.getLayoutManager().getClipToPadding()) {
      return view.getPaddingLeft();
    } else {
      return 0;
    }
  }
}
