function goToSpecies(name) {
  // 예: /post/list?speciesname=설치류
  window.location.href = `/post/list?speciesname=${encodeURIComponent(name)}`;
}
