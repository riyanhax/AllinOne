package com.parasme.swopinfo.socialedittext;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.MultiAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by @apradanas
 */
public class LinkableEditText extends MultiAutoCompleteTextView implements TextWatcher {

    private List<Link> mLinks = new ArrayList<>();
    OnTextCountListener onTextCount;
    private LinkModifier mLinkModifier;
    private OnTextChangedListener mOnTextChangedListener;

    public LinkableEditText(Context context) {
        super(context);

        init();
    }

    public LinkableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public LinkableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
//        setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        addTextChangedListener(this);
        setMovementMethod(LinkMovementMethod.getInstance());

        mLinkModifier = new LinkModifier(LinkModifier.ViewType.EDIT_TEXT);
    }

    public LinkableEditText addLink(Link link) {
        mLinks.add(link);

        mLinkModifier.setLinks(mLinks);

        return this;
    }

    public LinkableEditText addLinks(List<Link> links) {
        mLinks.addAll(links);

        mLinkModifier.setLinks(mLinks);

        return this;
    }

    public List<Link> getFoundLinks() {
        return mLinkModifier.getFoundLinks();
    }

    public LinkableEditText setTextChangedListener(OnTextChangedListener listener) {
        mOnTextChangedListener = listener;
        return this;
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if(mOnTextChangedListener != null) {
            mOnTextChangedListener.onTextChanged(text, start, lengthBefore, lengthAfter);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(mOnTextChangedListener != null) {
            mOnTextChangedListener.beforeTextChanged(s, start, count, after);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        String string = s.toString();
        if (string.length()==1 && !string.equals(string.toUpperCase())){
            string = string.toUpperCase();
            setText(string);
            setSelection(1);
        }

        mLinkModifier.setSpannable(s);

        mLinkModifier.build();

        if(mOnTextChangedListener != null) {
            mOnTextChangedListener.afterTextChanged(s);
        }

        onTextCount.onTextCount(1000 - s.toString().length() + "/1000");
    }

    public interface OnTextChangedListener {
        void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter);
        void beforeTextChanged(CharSequence s, int start, int count, int after);
        void afterTextChanged(Editable s);
    }

    public List<String> getHashTags(){
        List<String> hashTags = new ArrayList<>();
        Pattern pattern = Pattern.compile("(#\\w+)");
        Matcher matcher = pattern.matcher(getText().toString());
        while(matcher.find()){
            hashTags.add(matcher.group());
        }
        return hashTags;
    }

    public List<String> getMentions(){
        List<String> mentions = new ArrayList<>();
        Pattern pattern = Pattern.compile("(@\\w+)");
        Matcher matcher = pattern.matcher(getText().toString());
        while(matcher.find()){
            mentions.add(matcher.group());
        }
        return mentions;
    }

    public void setOnTextCountListener(OnTextCountListener onTextCount){
        this.onTextCount = onTextCount;
    }

    public interface OnTextCountListener{
        void onTextCount(String count);
    }
}