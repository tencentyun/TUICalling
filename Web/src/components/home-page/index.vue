<template>
  <div class="home-page-container">
    <div
      class="home-page-header"
    >Welcome {{loginUserInfo && (loginUserInfo.name || loginUserInfo.userId)}}</div>
    <div class="home-page-section-list">
      <div class="home-page-section" @click="goto('/video-call')">Video call</div>
      <div class="home-page-section" @click="goto('/audio-call')">Audio call</div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import {aegisReportEvent} from '../../utils/aegis'

export default {
  name: "HomePage",
  computed: mapState({
    loginUserInfo: state => state.loginUserInfo
  }),
  data() {
    return {
      enableEditName: true
    };
  },
  mounted() {
    aegisReportEvent("login", "login-success");
  },
  methods: {
    goto: function(path) {
      if (path.indexOf("video") !== -1) {
          aegisReportEvent("chooseSence", "VideoCall");
      } else if (path.indexOf("audio") !== -1) {
          aegisReportEvent("chooseSence", "VoiceCall");
      }
      this.$router.push(path);
    }
  }
};
</script>

<style scoped>
.home-page-header {
  padding: 30px 0 10px;
  font-size: 24px;
}
.home-page-section {
  background-image: linear-gradient(155deg, #0d2c5b 7%, #122755 93%);
  border-radius: 6px;
  color: #ffffff;
  width: 300px;
  height: 100px;
  margin: 10px auto;
  display: flex;
  align-items: center;
  justify-content: center;
}
.home-page-section:hover {
  cursor: pointer;
}
.change-name {
  margin-left: 10px;
  text-decoration: underline;
  color: #5f6368;
  font-size: 16px;
  cursor: pointer;
}
</style>
