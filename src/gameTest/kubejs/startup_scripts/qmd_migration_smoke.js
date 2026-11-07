// Tracked GameTest fixture; copied only by runGameTestServer -PwithKubeJS.
;(function () {
var QmdStartupSmokeBridge = Java.loadClass('lach_01298.qmd.compat.kubejs.KubeJSMigrationSmokeBridge')
try {
  var qmdSmokeNames = QMDParticles.names()
  if (qmdSmokeNames.size() !== 64) throw new Error('QMD particle catalog is incomplete')
  var qmdSmokeBeam = QMDParticles.stack('qmd:proton', 120, 1000000, 0.75)
  if (qmdSmokeBeam.getAmount() !== 120 || qmdSmokeBeam.getMeanEnergy() !== 1000000 || qmdSmokeBeam.getFocus() !== 0.75) {
    throw new Error('QMD numeric beam overload failed')
  }
  var qmdSmokeExactBeam = QMDParticles.stack('photon', 1, '9223372036854775807', 1)
  var qmdSmokeRejected = false
  try { QMDParticles.stack('proton', 1, 1.5, 0) } catch (qmdSmokeExpectedInvalidInput) { qmdSmokeRejected = true }
  if (!qmdSmokeRejected) throw new Error('QMD fractional beam energy was accepted')
  // Java checks the long directly so JavaScript numeric conversion cannot round it.
  QmdStartupSmokeBridge.startupPassed(qmdSmokeExactBeam)
  console.info('[QMD_SMOKE] Startup fixture passed: bindings, exact energy and invalid-input rejection')
} catch (qmdSmokeError) {
  QmdStartupSmokeBridge.failure('startup: ' + String(qmdSmokeError))
  throw qmdSmokeError
}
})()
