import { genTestUserSig } from "../../public/debug/GenerateTestUserSig"
const Aegis = window.Aegis;

class AegisReport {
    constructor() { 
        this.event = {};
        this.aegis = null;
    }
    init(options) {
        const { id, reportApiSpeed, reportAssetSpeed, spa } = options
        this.aegis = new Aegis({
            id, // 项目key
            reportApiSpeed, // 接口测速
            reportAssetSpeed, // 静态资源测速
            spa, // 开启页面测速
        });
    }
    reportEvent(name, ext1) {
        //去重处理，防止在一次login中重复上报其余事件 分母是login的总次数
        if (!this.event[name] || !this.event[name][ext1]) {
            this.aegis.reportEvent({
                name,
                ext1,
                ext2: "webTUICallingExternal",
                ext3: genTestUserSig('').sdkAppID,
            });
            if (typeof this.event[name] !== 'object') {
                this.event[name] = {};
            }
            this.event[name][ext1] = true;
        }
    }
}

const aegisReport = new AegisReport()
aegisReport.init({
    id: 'iHWefAYqQeozykLSdV', // 项目key
    reportApiSpeed: true, // 接口测速
    reportAssetSpeed: true, // 静态资源测速
    spa: true, // 开启页面测速
}) 

export const aegisReportEvent = aegisReport.reportEvent.bind(aegisReport)
