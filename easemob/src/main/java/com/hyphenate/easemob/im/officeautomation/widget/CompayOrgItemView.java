package com.hyphenate.easemob.im.officeautomation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.imlibs.officeautomation.domain.MPOrgEntity;

public class CompayOrgItemView extends LinearLayout {

    private View rlMyOrg;
    private View rlOrgStructure;

    private TextView tvMyOrg;
    private TextView tvCompanyName;
    private int companyId;
    private String companyName;

    private int orgId;
    private String orgName;

    private View llOrgsAndMyOrg;
    private View arrowUp;
    private View arrowDown;

    private ICompanyOrgClickListener listener;

    public CompayOrgItemView(Context context) {
        this(context, null);
    }

    public CompayOrgItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompayOrgItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.layout_compay_org, this);
        rlMyOrg = view.findViewById(R.id.rl_my_org);
        rlOrgStructure = view.findViewById(R.id.rl_org_structure);

        llOrgsAndMyOrg = view.findViewById(R.id.ll_orgs_and_myorg);

        arrowUp = view.findViewById(R.id.iv_org_up);
        arrowDown = view.findViewById(R.id.iv_org_down);

        tvMyOrg = view.findViewById(R.id.tv_my_org);
        tvCompanyName = view.findViewById(R.id.tv_my_company);
        rlOrgStructure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onStructureClicked(companyId, orgId);
                }

            }
        });
        rlMyOrg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onOrgClicked(companyId, orgId);
                }
            }
        });

        arrowUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                arrowUp.setVisibility(View.INVISIBLE);
                arrowDown.setVisibility(View.VISIBLE);
                llOrgsAndMyOrg.setVisibility(View.GONE);
            }
        });

        arrowDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                arrowDown.setVisibility(View.INVISIBLE);
                arrowUp.setVisibility(View.VISIBLE);
                llOrgsAndMyOrg.setVisibility(View.VISIBLE);
            }
        });

    }

    public void setCompanyAndOrgInfo(MPOrgEntity orgEntity) {
        this.orgId = orgEntity.getId();
        this.orgName = orgEntity.getName();
        this.companyId = orgEntity.getCompanyId();
        this.companyName = orgEntity.getCompanyName();
        tvCompanyName.setText(companyName);
        if (orgId >= 0) {
            tvMyOrg.setText(String.format("%s(我的部门)", orgName));
        } else {
            tvMyOrg.setText("我的部门");
        }
    }

    public void setItemClickListener(ICompanyOrgClickListener listener) {
        this.listener = listener;
    }


    public static interface ICompanyOrgClickListener {
        void onStructureClicked(int companyId, int orgId);
        void onOrgClicked(int companyId, int orgId);
    }



}
