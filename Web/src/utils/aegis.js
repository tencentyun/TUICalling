import {genTestUserSig} from "../../public/debug/GenerateTestUserSig"
const Aegis = window.Aegis;

const aegis = new Aegis({
    id: 'iHWefAYqQeozykLSdV', 
    reportApiSpeed: true, 
    reportAssetSpeed: true, 
    spa: true, 
});

export function aegisReportEvent(name, ext1) {
    if (!aegisReportEvent[name] || !aegisReportEvent[name][ext1]) {
        aegis.reportEvent({
            name,
            ext1,
            ext2: 'TRTCCallingExternal',
            ext3: genTestUserSig('').sdkAppID,
        });
        if(typeof aegisReportEvent[name] !== 'object') {
            aegisReportEvent[name] = {};
        }
        aegisReportEvent[name][ext1] = true;
    }
}