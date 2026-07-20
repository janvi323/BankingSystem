<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>DebtHues — My Loans</title>
<meta name="description" content="View all your active loans and manage EMI repayments with the progressive waterfall system.">
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
<style>
*,*::before,*::after{margin:0;padding:0;box-sizing:border-box;}
html{scroll-behavior:smooth;}
body{font-family:'Inter',-apple-system,BlinkMacSystemFont,sans-serif;background:#f5f3ff;color:#000;min-height:100vh;}
.nav{background:linear-gradient(135deg,#6d28d9 0%,#7c3aed 100%);height:62px;display:flex;align-items:center;padding:0 28px;justify-content:space-between;position:sticky;top:0;z-index:300;box-shadow:0 2px 24px rgba(109,40,217,.45);}
.nav-brand{color:#fff;font-size:1.18rem;font-weight:900;text-decoration:none;letter-spacing:-.025em;display:flex;align-items:center;gap:8px;}
.nav-brand-dot{width:8px;height:8px;background:#a78bfa;border-radius:50%;}
.nav-links{display:flex;gap:2px;align-items:center;}
.nav-links a,.nav-links button{color:rgba(255,255,255,.82);font-family:'Inter',sans-serif;font-size:.82rem;font-weight:600;padding:7px 13px;border-radius:8px;text-decoration:none;border:none;background:none;cursor:pointer;transition:background .15s,color .15s;}
.nav-links a:hover,.nav-links a.act,.nav-links button:hover{background:rgba(255,255,255,.18);color:#fff;}
.nav-links a.act{background:rgba(255,255,255,.22);color:#fff;}
.shell{max-width:920px;margin:0 auto;padding:32px 20px 100px;}
#toast{display:none;padding:14px 20px;border-radius:12px;font-size:.88rem;font-weight:700;color:#000;margin-bottom:20px;animation:slideDown .25s ease;}
#toast.ok{background:#d1fae5;border:1.5px solid #6ee7b7;}
#toast.err{background:#fee2e2;border:1.5px solid #fca5a5;}
#toast.inf{background:#ede9fe;border:1.5px solid #c4b5fd;}
@keyframes slideDown{from{opacity:0;transform:translateY(-8px)}to{opacity:1;transform:translateY(0)}}
#loading{text-align:center;padding:100px 20px;}
.spin-ring{width:52px;height:52px;border:4px solid #ede9fe;border-top-color:#7c3aed;border-radius:50%;animation:spin .75s linear infinite;margin:0 auto 18px;}
@keyframes spin{to{transform:rotate(360deg)}}
#loading p{font-size:.95rem;font-weight:600;color:#000;}
#empty{display:none;text-align:center;padding:80px 20px;background:#fff;border-radius:22px;border:2px solid #ede9fe;box-shadow:0 4px 24px rgba(124,58,237,.08);}
#empty .empty-ico{font-size:3.8rem;margin-bottom:16px;}
#empty h3{font-size:1.25rem;font-weight:900;color:#000;margin-bottom:8px;}
#empty p{font-size:.9rem;color:#000;margin-bottom:24px;font-weight:600;}
.cta-btn{display:inline-flex;align-items:center;gap:8px;background:#7c3aed;color:#fff;padding:13px 30px;border-radius:13px;font-weight:800;font-size:.9rem;text-decoration:none;transition:all .2s;box-shadow:0 4px 16px rgba(124,58,237,.3);}
.cta-btn:hover{background:#6d28d9;transform:translateY(-2px);}
#tier1{display:none;}
.dash-header{margin-bottom:28px;}
.dash-title{font-size:1.55rem;font-weight:900;color:#000;letter-spacing:-.03em;}
.dash-sub{font-size:.88rem;font-weight:600;color:#000;margin-top:6px;opacity:.7;}
.loan-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(290px,1fr));gap:20px;}
.loan-card{background:#fff;border-radius:18px;padding:24px;border:2px solid #ede9fe;border-left:5px solid #7c3aed;box-shadow:0 2px 16px rgba(124,58,237,.07);cursor:pointer;transition:all .25s ease;display:flex;flex-direction:column;position:relative;overflow:hidden;}
.loan-card::after{content:'';position:absolute;top:0;right:0;width:80px;height:80px;background:radial-gradient(circle at top right,rgba(124,58,237,.06),transparent 70%);pointer-events:none;}
.loan-card:hover{transform:translateY(-5px);border-color:#7c3aed;box-shadow:0 12px 40px rgba(124,58,237,.16);}
.lc-top{display:flex;align-items:flex-start;justify-content:space-between;margin-bottom:6px;}
.lc-purpose{font-size:1.05rem;font-weight:900;color:#000;letter-spacing:-.02em;}
.lc-status{background:#ede9fe;color:#5b21b6;border:1.5px solid #c4b5fd;padding:3px 10px;border-radius:100px;font-size:.68rem;font-weight:800;text-transform:uppercase;letter-spacing:.06em;white-space:nowrap;}
.lc-status.overdue{background:#fee2e2;color:#991b1b;border-color:#fca5a5;}
.lc-meta{font-size:.78rem;font-weight:600;color:#000;margin-bottom:18px;opacity:.65;}
.lc-overdue-pill{background:#fff5f5;border:1.5px solid #fecaca;border-radius:8px;padding:6px 10px;font-size:.74rem;font-weight:700;color:#000;display:flex;align-items:center;gap:6px;margin-bottom:14px;}
.lc-overdue-pill i{color:#dc2626;}
.lc-outstanding-lbl{font-size:.7rem;font-weight:700;color:#000;text-transform:uppercase;letter-spacing:.06em;margin-bottom:4px;opacity:.6;}
.lc-outstanding-val{font-size:1.65rem;font-weight:900;color:#000;letter-spacing:-.04em;margin-bottom:16px;}
.lc-prog-row{display:flex;justify-content:space-between;margin-bottom:6px;}
.lc-prog-lbl{font-size:.72rem;font-weight:700;color:#000;opacity:.65;}
.lc-prog-bg{background:#f0ebff;border-radius:100px;height:7px;overflow:hidden;margin-bottom:18px;}
.lc-prog-fg{height:100%;background:linear-gradient(90deg,#7c3aed,#a78bfa);border-radius:100px;transition:width .6s cubic-bezier(.25,.46,.45,.94);}
.lc-btn{margin-top:auto;width:100%;padding:11px;background:#7c3aed;color:#fff;border:none;border-radius:11px;font-family:'Inter',sans-serif;font-size:.85rem;font-weight:800;cursor:pointer;display:flex;align-items:center;justify-content:center;gap:8px;transition:all .2s;box-shadow:0 3px 12px rgba(124,58,237,.25);}
.lc-btn:hover{background:#6d28d9;box-shadow:0 6px 20px rgba(124,58,237,.35);}
#tier2{display:none;}
.back-bar{display:flex;align-items:center;gap:12px;margin-bottom:24px;}
.back-btn{display:inline-flex;align-items:center;gap:8px;background:#fff;border:2px solid #ede9fe;border-radius:10px;padding:9px 16px;font-family:'Inter',sans-serif;font-size:.84rem;font-weight:700;color:#000;cursor:pointer;transition:all .2s;}
.back-btn:hover{border-color:#7c3aed;background:#faf5ff;color:#7c3aed;}
.back-breadcrumb{font-size:.82rem;font-weight:600;color:#000;opacity:.5;}
.back-breadcrumb span{color:#7c3aed;font-weight:800;opacity:1;}
.loan-banner{background:#7c3aed;border-radius:18px;padding:22px 28px;margin-bottom:22px;display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:12px;box-shadow:0 6px 28px rgba(124,58,237,.3);}
.lb-title{font-size:1.2rem;font-weight:900;color:#fff;letter-spacing:-.02em;}
.lb-sub{font-size:.8rem;font-weight:600;color:rgba(255,255,255,.78);margin-top:4px;}
.lb-stats{display:flex;gap:20px;flex-wrap:wrap;}
.lb-stat{text-align:center;}
.lb-stat-val{font-size:1.1rem;font-weight:900;color:#fff;}
.lb-stat-lbl{font-size:.65rem;font-weight:700;color:rgba(255,255,255,.72);text-transform:uppercase;letter-spacing:.06em;margin-top:2px;}
.prog-card{background:#fff;border:2px solid #ede9fe;border-radius:14px;padding:18px 24px;margin-bottom:22px;box-shadow:0 1px 8px rgba(124,58,237,.06);}
.prog-row{display:flex;justify-content:space-between;align-items:center;margin-bottom:10px;}
.prog-row span{font-size:.82rem;font-weight:700;color:#000;}
.prog-bg{background:#ede9fe;border-radius:100px;height:12px;overflow:hidden;}
.prog-fg{height:100%;background:linear-gradient(90deg,#7c3aed,#8b5cf6,#a78bfa);border-radius:100px;transition:width 1s cubic-bezier(.25,.46,.45,.94);}
.wizard-card{background:#fff;border:2px solid #7c3aed;border-radius:22px;overflow:hidden;margin-bottom:22px;box-shadow:0 8px 40px rgba(124,58,237,.15);animation:fadeUp .35s ease;}
@keyframes fadeUp{from{opacity:0;transform:translateY(18px)}to{opacity:1;transform:translateY(0)}}
.wc-head{background:#7c3aed;padding:20px 28px;display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:10px;}
.wc-head-left{display:flex;align-items:center;gap:12px;}
.wc-num{background:rgba(255,255,255,.25);border-radius:50%;width:40px;height:40px;display:flex;align-items:center;justify-content:center;font-size:.92rem;font-weight:900;color:#fff;flex-shrink:0;}
.wc-title{font-size:1.02rem;font-weight:800;color:#fff;}
.wc-sub{font-size:.76rem;font-weight:600;color:rgba(255,255,255,.78);margin-top:2px;}
.wc-due{font-size:.82rem;font-weight:700;color:rgba(255,255,255,.9);display:flex;align-items:center;gap:6px;}
.wc-due.late{color:#fde68a;}
.wc-body{padding:26px 28px 22px;}
.amt-wrap{text-align:center;margin-bottom:22px;}
.amt-lbl{font-size:.72rem;font-weight:700;color:#000;text-transform:uppercase;letter-spacing:.08em;margin-bottom:6px;opacity:.6;}
.amt-big{font-size:3rem;font-weight:900;color:#000;letter-spacing:-.05em;line-height:1;}
.amt-break{font-size:.8rem;font-weight:600;color:#000;margin-top:8px;opacity:.7;}
.penalty-box{background:#fff5f5;border:2px solid #fecaca;border-radius:14px;padding:16px 20px;margin-bottom:18px;display:flex;align-items:flex-start;gap:14px;}
.pen-ico{font-size:1.5rem;color:#dc2626;flex-shrink:0;margin-top:2px;}
.pen-title{font-size:.9rem;font-weight:800;color:#000;}
.pen-detail{font-size:.78rem;font-weight:600;color:#000;margin-top:4px;line-height:1.55;}
.pen-help{display:inline-flex;align-items:center;gap:5px;margin-top:8px;font-size:.76rem;font-weight:800;color:#7c3aed;cursor:pointer;text-decoration:underline;background:none;border:none;font-family:'Inter',sans-serif;}
.partial-box{background:#fffbeb;border:1.5px solid #fde68a;border-radius:12px;padding:12px 16px;margin-bottom:18px;font-size:.82rem;font-weight:700;color:#000;display:flex;align-items:center;gap:8px;}
.wc-actions{display:grid;grid-template-columns:1fr 1fr;gap:12px;}
.btn-pay{grid-column:1/-1;padding:15px;background:#7c3aed;color:#fff;border:none;border-radius:13px;font-family:'Inter',sans-serif;font-size:.95rem;font-weight:800;cursor:pointer;display:flex;align-items:center;justify-content:center;gap:10px;transition:all .2s;box-shadow:0 4px 20px rgba(124,58,237,.3);}
.btn-pay:hover{background:#6d28d9;transform:translateY(-2px);box-shadow:0 8px 28px rgba(124,58,237,.4);}
.btn-pay:disabled{background:#a78bfa;cursor:not-allowed;transform:none;box-shadow:none;}
.btn-partial{padding:13px;background:#fff;color:#000;border:2px solid #7c3aed;border-radius:13px;font-family:'Inter',sans-serif;font-size:.85rem;font-weight:800;cursor:pointer;transition:all .2s;display:flex;align-items:center;justify-content:center;gap:8px;}
.btn-partial:hover{background:#faf5ff;border-color:#6d28d9;}
.btn-skip{padding:13px;background:#fff;color:#000;border:2px solid #e5e7eb;border-radius:13px;font-family:'Inter',sans-serif;font-size:.85rem;font-weight:700;cursor:pointer;transition:all .2s;display:flex;align-items:center;justify-content:center;gap:8px;}
.btn-skip:hover{background:#f9fafb;border-color:#d1d5db;}
.foreclose-card{background:#fff;border:2px dashed #10b981;border-radius:18px;padding:24px 28px;text-align:center;margin-bottom:22px;box-shadow:0 2px 12px rgba(16,185,129,.07);}
.fc-title{font-size:.95rem;font-weight:900;color:#000;margin-bottom:6px;}
.fc-desc{font-size:.82rem;font-weight:600;color:#000;margin-bottom:14px;opacity:.7;}
.fc-total{font-size:1.9rem;font-weight:900;color:#000;letter-spacing:-.04em;margin-bottom:16px;}
.btn-fc{padding:12px 30px;background:#10b981;color:#fff;border:none;border-radius:12px;font-family:'Inter',sans-serif;font-size:.9rem;font-weight:800;cursor:pointer;transition:all .2s;display:inline-flex;align-items:center;gap:8px;box-shadow:0 4px 14px rgba(16,185,129,.3);}
.btn-fc:hover{background:#059669;transform:translateY(-2px);}
.btn-fc:disabled{background:#6ee7b7;cursor:not-allowed;transform:none;}
.section-title{font-size:.75rem;font-weight:800;color:#000;text-transform:uppercase;letter-spacing:.08em;margin:22px 0 12px;display:flex;align-items:center;gap:10px;}
.section-title::after{content:'';flex:1;height:2px;background:#ede9fe;border-radius:2px;}
.hist-list,.queue-list{display:flex;flex-direction:column;gap:8px;margin-bottom:6px;}
.hist-item{background:#fff;border:1.5px solid #d1fae5;border-radius:13px;padding:14px 18px;display:flex;align-items:center;gap:14px;transition:transform .15s,box-shadow .15s;}
.hist-item:hover{transform:translateX(4px);box-shadow:0 3px 12px rgba(16,185,129,.12);}
.hist-check{width:34px;height:34px;background:#d1fae5;border-radius:50%;flex-shrink:0;display:flex;align-items:center;justify-content:center;color:#059669;font-size:.85rem;}
.hist-info{flex:1;}
.hist-emi{font-size:.88rem;font-weight:800;color:#000;}
.hist-date{font-size:.74rem;font-weight:600;color:#000;margin-top:3px;opacity:.65;}
.hist-amt{font-size:.95rem;font-weight:900;color:#059669;}
.show-more-btn{width:100%;padding:10px;background:none;border:1.5px solid #ede9fe;border-radius:10px;font-family:'Inter',sans-serif;font-size:.8rem;font-weight:700;color:#7c3aed;cursor:pointer;transition:all .2s;display:flex;align-items:center;justify-content:center;gap:6px;margin-top:6px;}
.show-more-btn:hover{background:#faf5ff;border-color:#7c3aed;}
.queue-item{background:#fafafa;border:1.5px solid #f3f4f6;border-radius:12px;padding:13px 18px;display:flex;align-items:center;gap:12px;opacity:.55;}
.q-num{width:30px;height:30px;background:#f3f4f6;border-radius:50%;flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:.72rem;font-weight:800;color:#000;}
.q-info{flex:1;}
.q-emi{font-size:.82rem;font-weight:700;color:#000;}
.q-date{font-size:.72rem;font-weight:600;color:#000;margin-top:2px;opacity:.65;}
.q-amt{font-size:.9rem;font-weight:800;color:#000;}
.q-lock{color:#9ca3af;font-size:.8rem;}
.done-card{background:#fff;border:2px solid #6ee7b7;border-radius:20px;padding:48px 40px;text-align:center;box-shadow:0 4px 24px rgba(16,185,129,.08);}
.done-emoji{font-size:4rem;margin-bottom:18px;display:block;animation:bounce .9s ease infinite alternate;}
@keyframes bounce{to{transform:translateY(-10px)}}
.done-card h3{font-size:1.3rem;font-weight:900;color:#000;margin-bottom:8px;}
.done-card p{font-size:.9rem;font-weight:600;color:#000;opacity:.7;}
.tenure-panel{background:#fff;border:2px solid #ede9fe;border-radius:18px;overflow:hidden;margin-bottom:22px;box-shadow:0 2px 12px rgba(124,58,237,.07);}
.tp-header{padding:16px 22px;display:flex;align-items:center;justify-content:space-between;cursor:pointer;user-select:none;transition:background .15s;}
.tp-header:hover{background:#faf5ff;}
.tp-header-left{display:flex;align-items:center;gap:10px;}
.tp-icon{width:36px;height:36px;background:#ede9fe;border-radius:10px;display:flex;align-items:center;justify-content:center;color:#7c3aed;font-size:.9rem;}
.tp-label{font-size:.92rem;font-weight:800;color:#000;}
.tp-sub{font-size:.74rem;font-weight:600;color:#000;opacity:.55;margin-top:2px;}
.tp-chevron{color:#7c3aed;font-size:.85rem;transition:transform .25s;}
.tp-chevron.open{transform:rotate(180deg);}
.tp-body{display:none;padding:20px 24px 24px;border-top:2px solid #ede9fe;}
.tp-body.open{display:block;}
.calc-grid{display:grid;grid-template-columns:1fr 1fr;gap:14px;margin-bottom:20px;}
.calc-box{background:#faf5ff;border:1.5px solid #ede9fe;border-radius:12px;padding:14px 16px;}
.calc-box-lbl{font-size:.68rem;font-weight:700;color:#000;text-transform:uppercase;letter-spacing:.06em;margin-bottom:4px;opacity:.6;}
.calc-box-val{font-size:1.1rem;font-weight:900;color:#000;}
.slider-section{margin-bottom:20px;}
.slider-label-row{display:flex;justify-content:space-between;align-items:center;margin-bottom:10px;}
.slider-label-row span{font-size:.82rem;font-weight:700;color:#000;}
.tenure-slider{width:100%;-webkit-appearance:none;appearance:none;height:6px;background:#ede9fe;border-radius:100px;outline:none;cursor:pointer;accent-color:#7c3aed;}
.tenure-slider::-webkit-slider-thumb{-webkit-appearance:none;width:20px;height:20px;border-radius:50%;background:#7c3aed;cursor:pointer;box-shadow:0 2px 8px rgba(124,58,237,.4);transition:transform .15s;}
.tenure-slider::-webkit-slider-thumb:hover{transform:scale(1.2);}
.slider-range{display:flex;justify-content:space-between;margin-top:6px;}
.slider-range span{font-size:.72rem;font-weight:600;color:#000;opacity:.5;}
.custom-row{display:flex;align-items:center;gap:10px;margin-bottom:20px;}
.custom-row label{font-size:.8rem;font-weight:700;color:#000;white-space:nowrap;}
.custom-input{flex:1;padding:9px 14px;border:2px solid #ede9fe;border-radius:10px;font-family:'Inter',sans-serif;font-size:.9rem;font-weight:700;color:#000;outline:none;transition:border-color .15s;background:#fff;max-width:120px;}
.custom-input:focus{border-color:#7c3aed;}
.custom-row span{font-size:.82rem;font-weight:600;color:#000;opacity:.6;}
.calc-result{background:#fff;border:2px solid #7c3aed;border-radius:14px;padding:18px 20px;}
.cr-title{font-size:.75rem;font-weight:800;color:#7c3aed;text-transform:uppercase;letter-spacing:.07em;margin-bottom:14px;}
.cr-rows{display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-bottom:14px;}
.cr-item{text-align:center;padding:12px;background:#faf5ff;border-radius:10px;}
.cr-item-lbl{font-size:.68rem;font-weight:700;color:#000;text-transform:uppercase;letter-spacing:.05em;margin-bottom:4px;opacity:.6;}
.cr-item-val{font-size:1rem;font-weight:900;color:#000;}
.cr-item-val.new-emi{color:#7c3aed;font-size:1.15rem;}
.cr-note{font-size:.75rem;font-weight:600;color:#000;opacity:.6;text-align:center;padding:10px;background:#faf5ff;border-radius:8px;}
.cr-action{width:100%;margin-top:14px;padding:12px;background:none;border:2px solid #7c3aed;border-radius:11px;font-family:'Inter',sans-serif;font-size:.85rem;font-weight:800;color:#7c3aed;cursor:pointer;transition:all .2s;display:flex;align-items:center;justify-content:center;gap:8px;}
.cr-action:hover{background:#7c3aed;color:#fff;}
.overlay{display:none;position:fixed;inset:0;background:rgba(0,0,0,.52);backdrop-filter:blur(5px);z-index:1000;align-items:center;justify-content:center;}
.overlay.open{display:flex;animation:fadeIn .2s ease;}
@keyframes fadeIn{from{opacity:0}to{opacity:1}}
.modal{background:#fff;border-radius:22px;width:92%;max-width:430px;padding:28px;box-shadow:0 20px 60px rgba(0,0,0,.28);animation:slideUp .25s ease;}
@keyframes slideUp{from{opacity:0;transform:translateY(20px)}to{opacity:1;transform:translateY(0)}}
.modal-h{font-size:1.08rem;font-weight:900;color:#000;margin-bottom:18px;display:flex;align-items:center;gap:10px;}
.modal-h i{color:#7c3aed;}
.modal-breakdown{background:#faf5ff;border:1.5px solid #ede9fe;border-radius:12px;padding:14px 16px;margin-bottom:18px;}
.mb-row{display:flex;justify-content:space-between;padding:4px 0;font-size:.85rem;font-weight:700;color:#000;}
.mb-row.total{border-top:2px solid #ede9fe;margin-top:8px;padding-top:10px;font-size:1rem;font-weight:900;}
.mb-row.credit{color:#059669;}
.m-label{display:block;font-size:.78rem;font-weight:700;color:#000;margin-bottom:6px;}
.m-input,.m-select{width:100%;padding:11px 14px;border:2px solid #e5e0f0;border-radius:10px;font-family:'Inter',sans-serif;font-size:.9rem;font-weight:600;color:#000;background:#fff;outline:none;transition:border-color .15s;margin-bottom:14px;}
.m-input:focus,.m-select:focus{border-color:#7c3aed;}
#partial-grp{display:none;}
.modal-footer{display:flex;gap:10px;margin-top:4px;}
.modal-footer button{flex:1;padding:13px;border-radius:12px;font-family:'Inter',sans-serif;font-size:.9rem;font-weight:800;cursor:pointer;border:none;transition:all .2s;}
#m-confirm{background:#7c3aed;color:#fff;box-shadow:0 4px 14px rgba(124,58,237,.25);}
#m-confirm:hover{background:#6d28d9;}
#m-confirm:disabled{background:#a78bfa;cursor:not-allowed;}
#m-cancel{background:#f3f4f6;color:#000;}
#m-cancel:hover{background:#e5e7eb;}
#tip-overlay{display:none;position:fixed;inset:0;background:rgba(0,0,0,.45);z-index:1100;align-items:center;justify-content:center;}
#tip-overlay.open{display:flex;animation:fadeIn .2s ease;}
.tip-box{background:#fff;border-radius:18px;padding:28px;max-width:380px;width:90%;box-shadow:0 16px 50px rgba(0,0,0,.22);}
.tip-box h4{font-size:1rem;font-weight:900;color:#000;margin-bottom:14px;display:flex;align-items:center;gap:8px;}
.tip-box h4 i{color:#7c3aed;}
.tip-row{display:flex;gap:12px;margin-bottom:10px;align-items:flex-start;}
.tip-dot{width:8px;height:8px;background:#7c3aed;border-radius:50%;flex-shrink:0;margin-top:5px;}
.tip-row p{font-size:.84rem;font-weight:600;color:#000;line-height:1.6;}
.tip-formula{background:#faf5ff;border:1.5px solid #ede9fe;border-radius:10px;padding:12px 16px;margin:14px 0;font-size:.84rem;font-weight:700;color:#000;text-align:center;line-height:1.7;}
.tip-close{width:100%;background:#7c3aed;color:#fff;border:none;border-radius:11px;padding:12px;font-family:'Inter',sans-serif;font-weight:800;cursor:pointer;font-size:.9rem;margin-top:6px;transition:background .15s;}
.tip-close:hover{background:#6d28d9;}
@media(max-width:600px){
  .shell{padding:20px 14px 80px;}
  .nav{padding:0 16px;}
  .nav-links a,.nav-links button{padding:6px 8px;font-size:.76rem;}
  .loan-grid{grid-template-columns:1fr;}
  .loan-banner{flex-direction:column;}
  .wc-body{padding:20px 18px 18px;}
  .wc-actions{grid-template-columns:1fr;}
  .btn-pay{grid-column:auto;}
  .amt-big{font-size:2.4rem;}
}
</style>
</head>
<body>
<nav class="nav">
  <a href="/dashboard" class="nav-brand"><div class="nav-brand-dot"></div>DebtHues</a>
  <div class="nav-links">
    <a href="/dashboard">Dashboard</a>
    <a href="/loans">Loans</a>
    <a href="/apply-loan">Apply</a>
    <a href="/emi" class="act">EMI</a>
    <a href="/hue">Hue</a>
    <form action="/perform_logout" method="post" style="display:inline;">
      <button type="submit">Logout</button>
    </form>
  </div>
</nav>
<div class="shell">
  <div id="toast" style="display:none;"></div>
  <div id="loading"><div class="spin-ring"></div><p>Loading your loans&hellip;</p></div>
  <div id="empty">
    <div class="empty-ico">&#128196;</div>
    <h3>No Active Loans</h3>
    <p>You don&rsquo;t have any approved loans with EMI schedules yet.</p>
    <a href="/apply-loan" class="cta-btn"><i class="fas fa-plus"></i> Apply for a Loan</a>
  </div>
  <div id="tier1">
    <div class="dash-header">
      <div class="dash-title">My Active Loans</div>
      <div class="dash-sub">Click any loan to manage repayments</div>
    </div>
    <div id="loan-grid" class="loan-grid"></div>
  </div>
  <div id="tier2">
    <div class="back-bar">
      <button class="back-btn" onclick="backToDashboard()"><i class="fas fa-arrow-left"></i> All Loans</button>
      <div class="back-breadcrumb">My Loans &rsaquo; <span id="bc-name">Loan</span></div>
    </div>
    <div class="loan-banner" id="loan-banner"></div>
    <div class="prog-card">
      <div class="prog-row"><span>&#128197; Repayment Progress</span><span id="prog-pct">0%</span></div>
      <div class="prog-bg"><div class="prog-fg" id="prog-fg" style="width:0%"></div></div>
    </div>
    <div id="wf-view"></div>
    <div class="tenure-panel" id="tenure-panel" style="display:none;">
      <div class="tp-header" onclick="toggleTenure()">
        <div class="tp-header-left">
          <div class="tp-icon"><i class="fas fa-sliders-h"></i></div>
          <div><div class="tp-label">EMI Restructure Calculator</div><div class="tp-sub">Adjust tenure &mdash; live EMI preview</div></div>
        </div>
        <i class="fas fa-chevron-down tp-chevron" id="tp-chevron"></i>
      </div>
      <div class="tp-body" id="tp-body">
        <div class="calc-grid">
          <div class="calc-box"><div class="calc-box-lbl">Outstanding</div><div class="calc-box-val" id="tp-outstanding">&mdash;</div></div>
          <div class="calc-box"><div class="calc-box-lbl">Interest Rate</div><div class="calc-box-val" id="tp-rate">&mdash;</div></div>
          <div class="calc-box"><div class="calc-box-lbl">Current EMI</div><div class="calc-box-val" id="tp-cur-emi">&mdash;</div></div>
          <div class="calc-box"><div class="calc-box-lbl">Remaining Months</div><div class="calc-box-val" id="tp-rem-months">&mdash;</div></div>
        </div>
        <div class="slider-section">
          <div class="slider-label-row"><span>Select New Tenure</span><span><strong id="slider-val">12</strong> months</span></div>
          <input type="range" class="tenure-slider" id="tenure-slider" min="2" max="60" value="12" oninput="onSlider(this.value)">
          <div class="slider-range"><span>2 months</span><span>60 months</span></div>
        </div>
        <div class="custom-row">
          <label for="custom-tenure">Custom months:</label>
          <input type="number" class="custom-input" id="custom-tenure" min="2" max="360" placeholder="e.g. 18" oninput="onCustomTenure(this.value)">
          <span>(2 &ndash; 360)</span>
        </div>
        <div class="calc-result">
          <div class="cr-title">&#9889; Live Calculation</div>
          <div class="cr-rows">
            <div class="cr-item"><div class="cr-item-lbl">Current EMI</div><div class="cr-item-val" id="cr-cur">&mdash;</div></div>
            <div class="cr-item"><div class="cr-item-lbl">New EMI</div><div class="cr-item-val new-emi" id="cr-new">&mdash;</div></div>
            <div class="cr-item"><div class="cr-item-lbl">Monthly Change</div><div class="cr-item-val" id="cr-diff">&mdash;</div></div>
            <div class="cr-item"><div class="cr-item-lbl">Total Interest</div><div class="cr-item-val" id="cr-interest">&mdash;</div></div>
          </div>
          <div class="cr-note" id="cr-note">Adjust the slider above to see a live preview.</div>
          <button class="cr-action" onclick="requestRestructure()"><i class="fas fa-paper-plane"></i> Request Restructure from Bank</button>
        </div>
      </div>
    </div>
  </div>
</div>
<div class="overlay" id="pay-modal" onclick="if(event.target===this)closePayModal()">
  <div class="modal">
    <div class="modal-h"><i class="fas fa-shield-alt"></i><span id="m-title">Pay EMI</span></div>
    <div class="modal-breakdown">
      <div class="mb-row"><span>EMI #<span id="m-num">&mdash;</span></span><span>Due <span id="m-due">&mdash;</span></span></div>
      <div class="mb-row"><span>Base Amount</span><span id="m-base">&mdash;</span></div>
      <div class="mb-row" id="m-pen-row" style="display:none;color:#dc2626;"><span>Late Fee</span><span id="m-pen">&mdash;</span></div>
      <div class="mb-row credit" id="m-part-row" style="display:none;"><span>Already Paid</span><span id="m-part">&mdash;</span></div>
      <div class="mb-row total"><span>Total Payable</span><span id="m-total" style="color:#7c3aed;">&mdash;</span></div>
    </div>
    <div id="partial-grp">
      <label class="m-label">Partial Amount (min &#8377;1, max <span id="m-max">&mdash;</span>)</label>
      <input type="number" class="m-input" id="m-amount" placeholder="Enter amount" min="1">
    </div>
    <label class="m-label">Payment Method</label>
    <select class="m-select" id="m-method">
      <option value="UPI">&#128241; UPI</option>
      <option value="Net Banking">&#128187; Net Banking</option>
      <option value="Debit Card">&#128179; Debit Card</option>
      <option value="Credit Card">&#128179; Credit Card</option>
    </select>
    <div class="modal-footer">
      <button id="m-confirm" onclick="submitPay()"><i class="fas fa-lock"></i> Pay Now</button>
      <button id="m-cancel" onclick="closePayModal()">Cancel</button>
    </div>
  </div>
</div>
<div id="tip-overlay" onclick="closeTip()">
  <div class="tip-box" onclick="event.stopPropagation()">
    <h4><i class="fas fa-info-circle"></i> How Late Fee Is Calculated</h4>
    <div class="tip-row"><div class="tip-dot"></div><p><strong>Grace Period:</strong> 3 days after due date &mdash; no penalty.</p></div>
    <div class="tip-row"><div class="tip-dot"></div><p><strong>Rate:</strong> 2% per month, pro-rated daily on the base EMI.</p></div>
    <div class="tip-formula">Late Fee = EMI &times; 2% &times; (Days Overdue &minus; 3) &divide; 30</div>
    <div class="tip-row"><div class="tip-dot"></div><p>Penalty resets to zero once EMI is paid in full.</p></div>
    <button class="tip-close" onclick="closeTip()">Got it</button>
  </div>
</div>
<script>
var S={view:'dashboard',loans:{},order:[],active:null,paying:null,payType:'FULL',histExpanded:false};
var RS='\u20b9';
function fmt(n){if(n==null||isNaN(n))return RS+'0';return RS+Number(n).toLocaleString('en-IN',{maximumFractionDigits:0});}
function fmtD(s){if(!s)return'\u2014';var d=new Date(s);return d.toLocaleDateString('en-IN',{day:'2-digit',month:'short',year:'numeric'});}
function daysOver(due){var now=new Date();now.setHours(0,0,0,0);var d=new Date(due);d.setHours(0,0,0,0);return Math.max(0,Math.floor((now-d)/86400000));}
function calcPenalty(e){if(e.status!=='PENDING')return 0;var od=daysOver(e.dueDate);if(od<=3)return 0;return Math.round(e.amount*0.02*((od-3)/30)*100)/100;}
function calcTotal(e){var pen=calcPenalty(e);var paid=e.partialAmountPaid||0;return Math.max(0,Math.round((e.amount+pen-paid)*100)/100);}
function calcEMI(P,ann,n){if(n<=0||P<=0)return 0;if(ann===0)return Math.round(P/n);var r=ann/12/100;var f=Math.pow(1+r,n);return Math.round(P*r*f/(f-1));}
function toast(msg,type){var el=document.getElementById('toast');el.innerHTML='<i class="fas fa-'+(type==='ok'?'check-circle':type==='err'?'exclamation-circle':'info-circle')+'"></i> '+msg;el.className=type;el.style.display='flex';el.style.alignItems='center';el.style.gap='10px';clearTimeout(toast._t);toast._t=setTimeout(function(){el.style.display='none';},6000);}
function group(emis){S.loans={};S.order=[];emis.forEach(function(e){var lid=e.loanId||0;if(!S.loans[lid]){S.loans[lid]={id:lid,purpose:e.purpose||'Loan',tenure:e.tenure||0,loanAmount:e.loanAmount||0,interestRate:e.interestRate||0,emiAmount:e.emiAmount||e.amount||0,bank:e.selectedBankName||'Bank',emis:[]};S.order.push(lid);}S.loans[lid].emis.push(e);});S.order.forEach(function(lid){S.loans[lid].emis.sort(function(a,b){return(a.emiNumber||0)-(b.emiNumber||0);});});}
function renderDashboard(){
  var grid=document.getElementById('loan-grid');var html='';
  S.order.forEach(function(lid){
    var l=S.loans[lid];
    var paid=l.emis.filter(function(e){return e.status==='PAID';});
    var pending=l.emis.filter(function(e){return e.status!=='PAID';});
    var overdue=pending.filter(function(e){return daysOver(e.dueDate)>0;});
    var pct=l.tenure>0?Math.round(paid.length*100/l.tenure):0;
    var outstanding=pending.reduce(function(s,e){return s+calcTotal(e);},0);
    var sCls=overdue.length>0?'lc-status overdue':'lc-status';
    var sTxt=overdue.length>0?overdue.length+' OVERDUE':'APPROVED';
    html+='<div class="loan-card" onclick="openWaterfall('+lid+')">';
    html+='<div class="lc-top"><div class="lc-purpose">'+l.purpose+'</div><div class="'+sCls+'">'+sTxt+'</div></div>';
    html+='<div class="lc-meta"><i class="fas fa-university" style="color:#a78bfa;margin-right:4px;"></i>'+l.bank+'&nbsp;&bull;&nbsp;'+l.interestRate+'% p.a.</div>';
    if(overdue.length>0){html+='<div class="lc-overdue-pill"><i class="fas fa-exclamation-triangle"></i>'+overdue.length+' EMI'+(overdue.length>1?'s':'')+' past due date</div>';}
    html+='<div class="lc-outstanding-lbl">Outstanding Balance</div>';
    html+='<div class="lc-outstanding-val">'+fmt(Math.round(outstanding))+'</div>';
    html+='<div class="lc-prog-row"><span class="lc-prog-lbl"><i class="fas fa-check-circle" style="color:#10b981;margin-right:3px;"></i>'+paid.length+' / '+l.tenure+' EMIs paid</span><span class="lc-prog-lbl">'+pct+'%</span></div>';
    html+='<div class="lc-prog-bg"><div class="lc-prog-fg" style="width:'+pct+'%;"></div></div>';
    html+='<button class="lc-btn"><i class="fas fa-arrow-right"></i> View Repayment Details</button>';
    html+='</div>';
  });
  grid.innerHTML=html;
}
function openWaterfall(loanId){S.active=loanId;S.view='waterfall';S.histExpanded=false;document.getElementById('tier1').style.display='none';document.getElementById('tier2').style.display='block';window.scrollTo({top:0,behavior:'smooth'});renderWaterfall();}
function backToDashboard(){S.view='dashboard';S.active=null;document.getElementById('tier2').style.display='none';document.getElementById('tier1').style.display='block';window.scrollTo({top:0,behavior:'smooth'});}
function renderWaterfall(){
  var l=S.loans[S.active];if(!l)return;
  var paid=l.emis.filter(function(e){return e.status==='PAID';});
  var pending=l.emis.filter(function(e){return e.status!=='PAID';});
  var next=pending.length>0?pending[0]:null;
  var upcoming=pending.slice(1);
  var pct=l.tenure>0?Math.round(paid.length*100/l.tenure):0;
  var outstanding=pending.reduce(function(s,e){return s+calcTotal(e);},0);
  document.getElementById('bc-name').textContent=l.purpose+' Loan';
  document.getElementById('loan-banner').innerHTML='<div><div class="lb-title"><i class="fas fa-university" style="margin-right:8px;opacity:.8;"></i>'+l.purpose+' Loan</div><div class="lb-sub">'+l.bank+'&nbsp;&bull;&nbsp;'+l.interestRate+'% p.a.&nbsp;&bull;&nbsp;'+l.tenure+' months</div></div><div class="lb-stats"><div class="lb-stat"><div class="lb-stat-val">'+fmt(l.loanAmount)+'</div><div class="lb-stat-lbl">Principal</div></div><div class="lb-stat"><div class="lb-stat-val">'+fmt(Math.round(outstanding))+'</div><div class="lb-stat-lbl">Outstanding</div></div><div class="lb-stat"><div class="lb-stat-val">'+paid.length+' / '+l.tenure+'</div><div class="lb-stat-lbl">EMIs Paid</div></div></div>';
  document.getElementById('prog-pct').textContent=pct+'% Complete';
  setTimeout(function(){document.getElementById('prog-fg').style.width=pct+'%';},80);
  var h='';
  if(!next){
    h='<div class="done-card"><span class="done-emoji">&#127881;</span><h3>Loan Fully Repaid!</h3><p>All '+l.tenure+' EMIs paid. Your loan is closed.</p></div>';
    document.getElementById('wf-view').innerHTML=h;document.getElementById('tenure-panel').style.display='none';return;
  }
  var od=daysOver(next.dueDate);var pen=calcPenalty(next);var partial=next.partialAmountPaid||0;var total=calcTotal(next);var isLate=od>0;
  h+='<div class="wizard-card"><div class="wc-head"><div class="wc-head-left"><div class="wc-num">#'+next.emiNumber+'</div><div><div class="wc-title">EMI '+next.emiNumber+' of '+l.tenure+'</div><div class="wc-sub">Next payment due</div></div></div>';
  h+='<div class="wc-due'+(isLate?' late':'')+'"><i class="fas fa-'+(isLate?'exclamation-triangle':'calendar-alt')+'"></i>'+(isLate?od+' day'+(od>1?'s':'')+' overdue':'Due '+fmtD(next.dueDate))+'</div></div>';
  h+='<div class="wc-body"><div class="amt-wrap"><div class="amt-lbl">Total Amount Due</div><div class="amt-big">'+fmt(total)+'</div><div class="amt-break">'+(pen>0?'Base '+fmt(next.amount)+' + Late Fee '+fmt(pen):'Base EMI amount')+(partial>0?' &minus; Paid '+fmt(partial):'')+'</div></div>';
  if(pen>0){h+='<div class="penalty-box"><div class="pen-ico"><i class="fas fa-gavel"></i></div><div><div class="pen-title">Late Fee Applied: '+fmt(pen)+'</div><div class="pen-detail">Due on '+fmtD(next.dueDate)+'. Fine of '+fmt(pen)+' added ('+(od-3)+' day'+(od-3>1?'s':'')+' past grace period).</div><button class="pen-help" onclick="openTip()"><i class="fas fa-info-circle"></i> How is this calculated?</button></div></div>';}
  if(partial>0){h+='<div class="partial-box"><i class="fas fa-coins" style="color:#d97706;"></i>Partial payment of '+fmt(partial)+' recorded &mdash; '+fmt(total)+' remaining</div>';}
  h+='<div class="wc-actions">';
  h+='<button class="btn-pay" onclick="openPayModal('+next.id+',\'FULL\','+total+','+next.amount+','+pen+','+partial+','+next.emiNumber+',\''+next.dueDate+'\')"><i class="fas fa-lock-open"></i> Pay This EMI &mdash; '+fmt(total)+'</button>';
  h+='<button class="btn-partial" onclick="openPayModal('+next.id+',\'PARTIAL\','+total+','+next.amount+','+pen+','+partial+','+next.emiNumber+',\''+next.dueDate+'\')"><i class="fas fa-coins"></i> Pay Partial</button>';
  if(upcoming.length>0){h+='<button class="btn-skip" onclick="skipToNext()"><i class="fas fa-forward"></i> Preview Next EMI</button>';}
  h+='</div></div></div>';
  if(pending.length>1){var fT=pending.reduce(function(s,e){return s+calcTotal(e);},0);h+='<div class="foreclose-card"><div class="fc-title"><i class="fas fa-flag-checkered" style="color:#10b981;margin-right:8px;"></i>Full Loan Settlement</div><div class="fc-desc">Close your entire loan now. All '+pending.length+' remaining EMIs will be marked paid.</div><div class="fc-total">'+fmt(Math.round(fT))+'</div><button class="btn-fc" id="fc-btn" onclick="foreclose('+l.id+','+Math.round(fT)+')"><i class="fas fa-handshake"></i> Settle Full Loan</button></div>';}
  if(paid.length>0){
    h+='<div class="section-title"><i class="fas fa-check-circle" style="color:#059669;"></i> Payment History</div><div class="hist-list">';
    var rev=paid.slice().reverse();var cnt=S.histExpanded?rev.length:Math.min(3,rev.length);
    for(var i=0;i<cnt;i++){var e=rev[i];h+='<div class="hist-item"><div class="hist-check"><i class="fas fa-check"></i></div><div class="hist-info"><div class="hist-emi">EMI #'+e.emiNumber+'</div><div class="hist-date">Due '+fmtD(e.dueDate)+' &bull; Paid '+fmtD(e.paymentDate)+(e.paymentMethod?' &bull; '+e.paymentMethod:'')+'</div></div><div class="hist-amt">'+fmt(e.amount)+'</div></div>';}
    h+='</div>';
    if(paid.length>3){h+='<button class="show-more-btn" onclick="toggleHistory()"><i class="fas fa-'+(S.histExpanded?'chevron-up':'chevron-down')+'"></i>'+(S.histExpanded?'Show Less':'Show '+(paid.length-3)+' More')+'</button>';}
  }
  if(upcoming.length>0){
    h+='<div class="section-title" style="margin-top:4px;"><i class="fas fa-lock" style="color:#9ca3af;"></i> Upcoming Queue (Locked)</div><div class="queue-list">';
    upcoming.forEach(function(e){h+='<div class="queue-item"><div class="q-num">'+e.emiNumber+'</div><div class="q-info"><div class="q-emi">EMI #'+e.emiNumber+'</div><div class="q-date">Due '+fmtD(e.dueDate)+'</div></div><div class="q-amt">'+fmt(e.amount)+'</div><i class="fas fa-lock q-lock"></i></div>';});
    h+='</div>';
  }
  document.getElementById('wf-view').innerHTML=h;
  initTenurePanel(l,pending,outstanding);
  document.getElementById('tenure-panel').style.display='block';
}
function toggleHistory(){S.histExpanded=!S.histExpanded;renderWaterfall();setTimeout(function(){var el=document.querySelector('.hist-list');if(el)el.scrollIntoView({behavior:'smooth',block:'start'});},100);}
function openPayModal(id,type,total,base,pen,partial,num,due){
  S.paying={emiId:id,type:type,total:total};S.payType=type;
  document.getElementById('m-title').textContent=type==='PARTIAL'?'Partial Payment':'Pay EMI #'+num;
  document.getElementById('m-num').textContent=num;
  document.getElementById('m-due').textContent=fmtD(due);
  document.getElementById('m-base').textContent=fmt(base);
  document.getElementById('m-total').textContent=fmt(total);
  document.getElementById('m-max').textContent=fmt(total);
  document.getElementById('m-amount').value='';
  document.getElementById('m-amount').max=total;
  var pr=document.getElementById('m-pen-row');pr.style.display=pen>0?'flex':'none';if(pen>0)document.getElementById('m-pen').textContent='+'+fmt(pen);
  var pa=document.getElementById('m-part-row');pa.style.display=partial>0?'flex':'none';if(partial>0)document.getElementById('m-part').textContent='-'+fmt(partial);
  document.getElementById('partial-grp').style.display=type==='PARTIAL'?'block':'none';
  var btn=document.getElementById('m-confirm');btn.disabled=false;btn.innerHTML='<i class="fas fa-lock"></i> '+(type==='PARTIAL'?'Submit Payment':'Pay Now');
  document.getElementById('pay-modal').classList.add('open');
}
function closePayModal(){document.getElementById('pay-modal').classList.remove('open');S.paying=null;}
function submitPay(){
  var p=S.paying;if(!p)return;
  var method=document.getElementById('m-method').value;var amt=null;
  if(S.payType==='PARTIAL'){amt=parseFloat(document.getElementById('m-amount').value);if(!amt||amt<=0){toast('Please enter a valid amount.','err');return;}if(amt>p.total){toast('Amount exceeds total payable.','err');return;}}
  var btn=document.getElementById('m-confirm');btn.disabled=true;btn.innerHTML='<i class="fas fa-circle-notch fa-spin"></i> Processing&hellip;';
  var url,body;
  if(S.payType==='PARTIAL'){url='/api/emi/pay-partial/'+p.emiId;body=JSON.stringify({amount:amt,paymentMethod:method});}
  else{url='/api/emi/pay/'+p.emiId;body=JSON.stringify({paymentMethod:method});}
  fetch(url,{method:'POST',headers:{'Content-Type':'application/json'},body:body})
    .then(function(r){if(!r.ok)throw new Error('HTTP '+r.status);return r.text();})
    .then(function(){closePayModal();toast(S.payType==='PARTIAL'?'\uD83D\uDCB0 Partial payment recorded!':'\uD83C\uDF89 EMI paid! Next one is now active.','ok');setTimeout(function(){loadData(true);},700);})
    .catch(function(e){toast('Payment failed: '+e.message,'err');btn.disabled=false;btn.innerHTML='<i class="fas fa-lock"></i> Pay Now';});
}
function foreclose(loanId,total){
  if(!confirm('Settle the entire loan for '+fmt(total)+'?\n\nAll remaining EMIs will be marked as PAID.'))return;
  var btn=document.getElementById('fc-btn');if(btn){btn.disabled=true;btn.innerHTML='<i class="fas fa-circle-notch fa-spin"></i> Settling&hellip;';}
  fetch('/api/emi/pay-full-loan/'+loanId,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({paymentMethod:'UPI (Foreclosure)'})})
    .then(function(r){if(!r.ok)throw new Error('HTTP '+r.status);return r.text();})
    .then(function(){toast('\uD83C\uDF81 Loan fully settled! All EMIs paid.','ok');setTimeout(function(){loadData(true);},700);})
    .catch(function(e){toast('Settlement failed: '+e.message,'err');if(btn){btn.disabled=false;btn.innerHTML='<i class="fas fa-handshake"></i> Settle Full Loan';}});
}
function skipToNext(){var l=S.loans[S.active];if(!l)return;var pending=l.emis.filter(function(e){return e.status!=='PAID';});if(pending.length<2){toast('No next EMI available.','inf');return;}var n=pending[1];openPayModal(n.id,'FULL',calcTotal(n),n.amount,calcPenalty(n),n.partialAmountPaid||0,n.emiNumber,n.dueDate);}
var _tp={outstanding:0,rate:0,curEMI:0,curMonths:0};
function initTenurePanel(l,pending,outstanding){
  var rem=pending.length;_tp.outstanding=Math.round(outstanding);_tp.rate=l.interestRate;_tp.curEMI=l.emiAmount;_tp.curMonths=rem;
  document.getElementById('tp-outstanding').textContent=fmt(_tp.outstanding);
  document.getElementById('tp-rate').textContent=_tp.rate+'% p.a.';
  document.getElementById('tp-cur-emi').textContent=fmt(_tp.curEMI);
  document.getElementById('tp-rem-months').textContent=rem+' months';
  var sl=document.getElementById('tenure-slider');sl.value=Math.min(Math.max(rem,2),60);
  document.getElementById('custom-tenure').value='';
  document.getElementById('slider-val').textContent=sl.value;
  updateCalc(parseInt(sl.value));
}
function toggleTenure(){var b=document.getElementById('tp-body');var c=document.getElementById('tp-chevron');var o=b.classList.contains('open');b.classList.toggle('open',!o);c.classList.toggle('open',!o);}
function onSlider(v){v=parseInt(v);document.getElementById('slider-val').textContent=v;document.getElementById('custom-tenure').value=v;updateCalc(v);}
function onCustomTenure(v){v=parseInt(v);if(isNaN(v)||v<2)return;var sl=document.getElementById('tenure-slider');sl.value=Math.min(v,60);document.getElementById('slider-val').textContent=Math.min(v,60);updateCalc(v);}
function updateCalc(months){
  if(!months||months<2)return;
  var ne=calcEMI(_tp.outstanding,_tp.rate,months);var ce=_tp.curEMI;var diff=ce-ne;
  var ei=Math.max(0,ne*months-_tp.outstanding);
  document.getElementById('cr-cur').textContent=fmt(ce);
  document.getElementById('cr-new').textContent=fmt(ne);
  var de=document.getElementById('cr-diff');
  if(diff>0){de.textContent='\u2212'+fmt(diff)+'/mo';de.style.color='#059669';}
  else if(diff<0){de.textContent='+'+fmt(Math.abs(diff))+'/mo';de.style.color='#dc2626';}
  else{de.textContent='Same';de.style.color='#000';}
  document.getElementById('cr-interest').textContent=fmt(Math.round(ei));
  var note='';
  if(months>_tp.curMonths){note='\u2139\uFE0F Extending by '+(months-_tp.curMonths)+' months lowers monthly burden by '+fmt(Math.abs(diff))+', but adds '+fmt(Math.round(ei-(ce*_tp.curMonths-_tp.outstanding)))+' extra interest.';}
  else if(months<_tp.curMonths){note='\u2139\uFE0F Shorter tenure = higher EMI but less total interest paid.';}
  else{note='This matches your current remaining tenure. Adjust to explore options.';}
  document.getElementById('cr-note').textContent=note;
}
function requestRestructure(){var l=S.loans[S.active];toast('\uD83D\uDCE7 Restructure request sent to '+(l?l.bank:'bank')+'. They will contact you within 2\u20133 business days.','inf');}
function openTip(){document.getElementById('tip-overlay').classList.add('open');}
function closeTip(){document.getElementById('tip-overlay').classList.remove('open');}
function loadData(keepLoan){
  fetch('/api/emi/my-emis-safe')
    .then(function(r){if(r.status===401){window.location.href='/login';return null;}if(!r.ok)throw new Error('HTTP '+r.status);return r.json();})
    .then(function(data){
      if(!data)return;
      document.getElementById('loading').style.display='none';
      if(!data.length){
        fetch('/api/emi/generate-my-emis',{method:'POST'}).then(function(r){return r.text();}).then(function(msg){
          if(msg&&msg.indexOf('Generated EMIs for 0')===-1&&msg.indexOf('Generated')!==-1){setTimeout(function(){loadData(false);},700);}
          else{document.getElementById('empty').style.display='block';}
        }).catch(function(){document.getElementById('empty').style.display='block';});
        return;
      }
      group(data);
      if(S.view==='dashboard'||!keepLoan||!S.active||!S.loans[S.active]){
        document.getElementById('tier1').style.display='block';
        document.getElementById('tier2').style.display='none';
        document.getElementById('empty').style.display='none';
        S.view='dashboard';renderDashboard();
      } else {renderWaterfall();}
    })
    .catch(function(e){document.getElementById('loading').style.display='none';toast('Failed to load: '+e.message,'err');});
}
loadData(false);
setInterval(function(){loadData(true);},30000);
</script>
<%@ include file="fragments/hue-chatbot-widget.jspf" %>
</body>
</html>
